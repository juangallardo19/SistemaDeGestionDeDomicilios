package co.edu.ucc.domicilios.servicio;

import co.edu.ucc.domicilios.dominio.pedido.Pedido;
import co.edu.ucc.domicilios.dominio.ubicacion.Ruta;
import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;
import co.edu.ucc.domicilios.dominio.usuario.Repartidor;
import co.edu.ucc.domicilios.dto.PedidoDTO;
import co.edu.ucc.domicilios.dto.RepartidorDTO;
import co.edu.ucc.domicilios.dto.SeguimientoDTO;
import co.edu.ucc.domicilios.excepcion.RecursoNoEncontradoException;
import co.edu.ucc.domicilios.excepcion.ReglaNegocioException;
import co.edu.ucc.domicilios.patrones.evento.EventoPedido;
import co.edu.ucc.domicilios.patrones.evento.PublicadorEventosPedido;
import co.edu.ucc.domicilios.patrones.evento.TipoEvento;
import co.edu.ucc.domicilios.repositorio.RepartidorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/** Casos de uso del repartidor: ver sus pedidos, reportar ubicación y manejar su turno. */
@Service
@Transactional
public class ServicioRepartidor {

    private final RepartidorRepository repartidores;
    private final ServicioSeguimiento seguimiento;
    private final ConfiguracionEstrategias estrategias;
    private final PublicadorEventosPedido publicador;

    public ServicioRepartidor(RepartidorRepository repartidores, ServicioSeguimiento seguimiento,
                              ConfiguracionEstrategias estrategias, PublicadorEventosPedido publicador) {
        this.repartidores = repartidores;
        this.seguimiento = seguimiento;
        this.estrategias = estrategias;
        this.publicador = publicador;
    }

    @Transactional(readOnly = true)
    public List<RepartidorDTO> listar() {
        return repartidores.findAll().stream().map(RepartidorDTO::desde).toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> pedidosActivos(Long repartidorId) {
        return buscar(repartidorId).getPedidosActivos().stream().map(PedidoDTO::desde).toList();
    }

    /** El repartidor reporta su posición (GPS); la ruta se sincroniza y se avisa al cliente. */
    public SeguimientoDTO actualizarUbicacion(Long repartidorId, Ubicacion ubicacion) {
        Repartidor repartidor = buscar(repartidorId);
        repartidor.actualizarUbicacion(ubicacion);
        Pedido pedido = pedidoEnRuta(repartidor);
        pedido.getRuta().sincronizarCon(ubicacion);
        return notificarAvance(pedido);
    }

    /** Simulación: mueve al repartidor al siguiente punto de su ruta. */
    public SeguimientoDTO avanzar(Long repartidorId) {
        Repartidor repartidor = buscar(repartidorId);
        Pedido pedido = pedidoEnRuta(repartidor);
        Ruta ruta = pedido.getRuta();
        ruta.avanzar();
        repartidor.actualizarUbicacion(ruta.posicionActual());
        return notificarAvance(pedido);
    }

    public RepartidorDTO cambiarTurno(Long repartidorId, boolean enTurno) {
        Repartidor repartidor = buscar(repartidorId);
        if (enTurno) {
            repartidor.iniciarTurno();
        } else {
            repartidor.terminarTurno();
        }
        return RepartidorDTO.desde(repartidor);
    }

    private SeguimientoDTO notificarAvance(Pedido pedido) {
        SeguimientoDTO estado = seguimiento.armar(pedido, estrategias.calculadorTiempo());
        String mensaje = pedido.getRuta().haLlegado()
                ? "el repartidor llegó a tu dirección"
                : String.format(Locale.US, "el repartidor está a %.2f km, llega en ~%d min (%d%% del recorrido)",
                estado.distanciaRestanteKm(), estado.minutosEstimados(), estado.progreso());
        publicador.publicar(EventoPedido.de(TipoEvento.UBICACION_ACTUALIZADA, pedido.getId(),
                pedido.getCliente().getNombre(), pedido.getEstado().name(), mensaje));
        return estado;
    }

    private Pedido pedidoEnRuta(Repartidor repartidor) {
        return repartidor.getPedidosActivos().stream()
                .filter(p -> p.getRuta() != null)
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException(repartidor.getNombre() + " no tiene pedidos en camino"));
    }

    private Repartidor buscar(Long id) {
        return repartidores.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Repartidor", id));
    }
}
