package co.edu.ucc.domicilios.servicio;

import co.edu.ucc.domicilios.dominio.establecimiento.Establecimiento;
import co.edu.ucc.domicilios.dominio.establecimiento.Inventario;
import co.edu.ucc.domicilios.dominio.establecimiento.Producto;
import co.edu.ucc.domicilios.dominio.pedido.Pedido;
import co.edu.ucc.domicilios.dominio.usuario.Cliente;
import co.edu.ucc.domicilios.dto.CotizacionDTO;
import co.edu.ucc.domicilios.dto.ItemSolicitud;
import co.edu.ucc.domicilios.dto.PedidoDTO;
import co.edu.ucc.domicilios.dto.SolicitudPedido;
import co.edu.ucc.domicilios.excepcion.RecursoNoEncontradoException;
import co.edu.ucc.domicilios.excepcion.StockInsuficienteException;
import co.edu.ucc.domicilios.patrones.descuento.Descuento;
import co.edu.ucc.domicilios.patrones.descuento.DetalleCosto;
import co.edu.ucc.domicilios.patrones.descuento.FabricaDescuentos;
import co.edu.ucc.domicilios.patrones.estado.EstadoPedidoTipo;
import co.edu.ucc.domicilios.patrones.evento.EventoPedido;
import co.edu.ucc.domicilios.patrones.evento.PublicadorEventosPedido;
import co.edu.ucc.domicilios.patrones.evento.TipoEvento;
import co.edu.ucc.domicilios.patrones.pago.FabricaMetodosPago;
import co.edu.ucc.domicilios.patrones.pago.ResultadoPago;
import co.edu.ucc.domicilios.repositorio.ClienteRepository;
import co.edu.ucc.domicilios.repositorio.EstablecimientoRepository;
import co.edu.ucc.domicilios.repositorio.PedidoRepository;
import co.edu.ucc.domicilios.repositorio.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Casos de uso del cliente: cotizar, hacer, consultar y cancelar pedidos. */
@Service
@Transactional
public class ServicioPedidos {

    private final PedidoRepository pedidos;
    private final ClienteRepository clientes;
    private final EstablecimientoRepository establecimientos;
    private final ProductoRepository productos;
    private final FabricaDescuentos fabricaDescuentos;
    private final FabricaMetodosPago fabricaMetodosPago;
    private final CalculadorCostoEnvio calculadorCostoEnvio;
    private final PublicadorEventosPedido publicador;

    public ServicioPedidos(PedidoRepository pedidos, ClienteRepository clientes,
                           EstablecimientoRepository establecimientos, ProductoRepository productos,
                           FabricaDescuentos fabricaDescuentos, FabricaMetodosPago fabricaMetodosPago,
                           CalculadorCostoEnvio calculadorCostoEnvio, PublicadorEventosPedido publicador) {
        this.pedidos = pedidos;
        this.clientes = clientes;
        this.establecimientos = establecimientos;
        this.productos = productos;
        this.fabricaDescuentos = fabricaDescuentos;
        this.fabricaMetodosPago = fabricaMetodosPago;
        this.calculadorCostoEnvio = calculadorCostoEnvio;
        this.publicador = publicador;
    }

    /** Calcula el total sin crear el pedido (sirve para mostrar el resumen antes de pagar). */
    @Transactional(readOnly = true)
    public CotizacionDTO cotizar(SolicitudPedido solicitud) {
        Pedido pedido = armarPedido(solicitud);
        liquidar(pedido, solicitud.codigosDescuento());
        double distancia = pedido.getEstablecimiento().getUbicacion().distanciaKmA(pedido.getDestino());
        return new CotizacionDTO(pedido.getSubtotal(), pedido.getCostoEnvio(), pedido.getDescuento(),
                pedido.getTotal(), pedido.getDescuentosAplicados(), Math.round(distancia * 100) / 100.0);
    }

    /** Pedido -> verificación de existencias -> descuentos -> pago -> estado RECIBIDO. */
    public PedidoDTO crear(SolicitudPedido solicitud) {
        Pedido pedido = armarPedido(solicitud);
        liquidar(pedido, solicitud.codigosDescuento());
        ResultadoPago pago = pedido.pagar(fabricaMetodosPago.obtener(solicitud.metodoPago()));
        pedidos.save(pedido);
        notificar(pedido, "fue recibido por " + pedido.getEstablecimiento().getNombre() + ". " + pago.mensaje());
        return PedidoDTO.desde(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoDTO consultar(Long pedidoId) {
        return PedidoDTO.desde(buscar(pedidoId));
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> pedidosDeCliente(Long clienteId) {
        return pedidos.findByClienteIdOrderByCreadoEnDesc(clienteId).stream().map(PedidoDTO::desde).toList();
    }

    public PedidoDTO cancelar(Long pedidoId) {
        Pedido pedido = buscar(pedidoId);
        pedido.cancelar();
        notificar(pedido, "fue cancelado");
        return PedidoDTO.desde(pedido);
    }

    private Pedido armarPedido(SolicitudPedido solicitud) {
        Cliente cliente = clientes.findById(solicitud.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", solicitud.clienteId()));
        Establecimiento establecimiento = establecimientos.findById(solicitud.establecimientoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Establecimiento", solicitud.establecimientoId()));

        Pedido pedido = new Pedido(cliente, establecimiento);
        for (ItemSolicitud item : solicitud.items()) {
            Producto producto = productos.findById(item.productoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto", item.productoId()));
            pedido.agregarItem(producto, item.cantidad());
        }
        verificarExistencias(establecimiento.getInventario(), pedido);
        return pedido;
    }

    /** No se aceptan pedidos de productos que el establecimiento ya no tiene. */
    private void verificarExistencias(Inventario inventario, Pedido pedido) {
        pedido.cantidadesPorProducto().forEach((producto, cantidad) -> {
            if (!inventario.hayDisponibilidad(producto, cantidad)) {
                throw new StockInsuficienteException(producto.getNombre(), cantidad,
                        inventario.cantidadDisponible(producto));
            }
        });
    }

    private void liquidar(Pedido pedido, List<String> codigosDescuento) {
        double costoEnvio = calculadorCostoEnvio.calcular(pedido.getEstablecimiento().getUbicacion(), pedido.getDestino());
        boolean esPrimeraCompra = pedidos.countByClienteIdAndEstadoNot(
                pedido.getCliente().getId(), EstadoPedidoTipo.CANCELADO) == 0;
        DetalleCosto costo = new DetalleCosto(pedido.calcularSubtotal(), costoEnvio);
        Descuento descuento = fabricaDescuentos.crear(codigosDescuento, costo, esPrimeraCompra);
        pedido.liquidar(costoEnvio, descuento);
    }

    private Pedido buscar(Long id) {
        return pedidos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Pedido", id));
    }

    private void notificar(Pedido pedido, String mensaje) {
        publicador.publicar(EventoPedido.de(TipoEvento.ESTADO_CAMBIADO, pedido.getId(),
                pedido.getCliente().getNombre(), pedido.getEstado().name(), mensaje));
    }
}
