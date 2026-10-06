package co.edu.ucc.domicilios.servicio;

import co.edu.ucc.domicilios.dominio.establecimiento.Establecimiento;
import co.edu.ucc.domicilios.dominio.pedido.Pedido;
import co.edu.ucc.domicilios.dominio.ubicacion.Ruta;
import co.edu.ucc.domicilios.dominio.usuario.Repartidor;
import co.edu.ucc.domicilios.dto.PedidoDTO;
import co.edu.ucc.domicilios.excepcion.RecursoNoEncontradoException;
import co.edu.ucc.domicilios.excepcion.ReglaNegocioException;
import co.edu.ucc.domicilios.patrones.asignacion.EstrategiaAsignacion;
import co.edu.ucc.domicilios.patrones.evento.EventoPedido;
import co.edu.ucc.domicilios.patrones.evento.PublicadorEventosPedido;
import co.edu.ucc.domicilios.patrones.evento.TipoEvento;
import co.edu.ucc.domicilios.repositorio.PedidoRepository;
import co.edu.ucc.domicilios.repositorio.RepartidorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Avanza el pedido por su ciclo de vida: preparación, asignación del repartidor y entrega. */
@Service
@Transactional
public class ServicioDespacho {

    private final PedidoRepository pedidos;
    private final RepartidorRepository repartidores;
    private final ConfiguracionEstrategias estrategias;
    private final PublicadorEventosPedido publicador;

    public ServicioDespacho(PedidoRepository pedidos, RepartidorRepository repartidores,
                            ConfiguracionEstrategias estrategias, PublicadorEventosPedido publicador) {
        this.pedidos = pedidos;
        this.repartidores = repartidores;
        this.estrategias = estrategias;
        this.publicador = publicador;
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> pedidosDeEstablecimiento(Long establecimientoId) {
        return pedidos.findByEstablecimientoIdOrderByCreadoEnDesc(establecimientoId).stream()
                .map(PedidoDTO::desde).toList();
    }

    /** El establecimiento empieza a preparar el pedido y se descuenta el inventario. */
    public PedidoDTO preparar(Long pedidoId) {
        Pedido pedido = buscar(pedidoId);
        pedido.iniciarPreparacion();
        pedido.getEstablecimiento().getInventario().descontar(pedido.cantidadesPorProducto());
        notificar(pedido, "se está preparando en " + pedido.getEstablecimiento().getNombre());
        return PedidoDTO.desde(pedido);
    }

    /** Se asigna un repartidor con la estrategia activa y se traza su ruta hasta el cliente. */
    public PedidoDTO despachar(Long pedidoId) {
        Pedido pedido = buscar(pedidoId);
        Establecimiento origen = pedido.getEstablecimiento();
        EstrategiaAsignacion estrategia = estrategias.asignacion();

        List<Repartidor> disponibles = repartidores.findByEnTurnoTrue().stream()
                .filter(Repartidor::estaDisponible)
                .toList();
        Repartidor repartidor = estrategia.seleccionar(disponibles, origen.getUbicacion())
                .orElseThrow(() -> new ReglaNegocioException("No hay repartidores disponibles en este momento"));

        pedido.despachar(repartidor, Ruta.trazar(origen.getUbicacion(), pedido.getDestino()));
        repartidor.actualizarUbicacion(origen.getUbicacion());
        notificar(pedido, "va en camino con " + repartidor.getNombre() + " (" + repartidor.getVehiculo()
                + "), asignado con la estrategia '" + estrategia.nombre() + "'");
        return PedidoDTO.desde(pedido);
    }

    /** El repartidor confirma la entrega. */
    public PedidoDTO entregar(Long pedidoId) {
        Pedido pedido = buscar(pedidoId);
        pedido.marcarEntregado();
        notificar(pedido, "fue entregado. ¡Buen provecho!");
        return PedidoDTO.desde(pedido);
    }

    private Pedido buscar(Long id) {
        return pedidos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Pedido", id));
    }

    private void notificar(Pedido pedido, String mensaje) {
        publicador.publicar(EventoPedido.de(TipoEvento.ESTADO_CAMBIADO, pedido.getId(),
                pedido.getCliente().getNombre(), pedido.getEstado().name(), mensaje));
    }
}
