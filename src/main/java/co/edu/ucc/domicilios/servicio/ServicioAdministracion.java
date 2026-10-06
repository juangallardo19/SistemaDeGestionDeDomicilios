package co.edu.ucc.domicilios.servicio;

import co.edu.ucc.domicilios.dominio.establecimiento.Establecimiento;
import co.edu.ucc.domicilios.dominio.establecimiento.ItemInventario;
import co.edu.ucc.domicilios.dominio.establecimiento.NivelStock;
import co.edu.ucc.domicilios.dominio.pedido.ItemPedido;
import co.edu.ucc.domicilios.dominio.pedido.Pedido;
import co.edu.ucc.domicilios.dominio.usuario.Repartidor;
import co.edu.ucc.domicilios.dto.CargaRepartidorDTO;
import co.edu.ucc.domicilios.dto.CargaRepartidorDTO.PedidoEnRuta;
import co.edu.ucc.domicilios.dto.CargaRepartidorDTO.ProductoCantidad;
import co.edu.ucc.domicilios.dto.DisponibilidadEstablecimientoDTO;
import co.edu.ucc.domicilios.dto.EstrategiasDTO;
import co.edu.ucc.domicilios.dto.ItemInventarioDTO;
import co.edu.ucc.domicilios.dto.UbicacionDTO;
import co.edu.ucc.domicilios.patrones.evento.BitacoraPedidos;
import co.edu.ucc.domicilios.patrones.evento.EventoPedido;
import co.edu.ucc.domicilios.repositorio.EstablecimientoRepository;
import co.edu.ucc.domicilios.repositorio.RepartidorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Consultas del administrador: productos por repartidor y disponibilidad por establecimiento. */
@Service
@Transactional(readOnly = true)
public class ServicioAdministracion {

    private final RepartidorRepository repartidores;
    private final EstablecimientoRepository establecimientos;
    private final ConfiguracionEstrategias estrategias;
    private final BitacoraPedidos bitacora;

    public ServicioAdministracion(RepartidorRepository repartidores, EstablecimientoRepository establecimientos,
                                  ConfiguracionEstrategias estrategias, BitacoraPedidos bitacora) {
        this.repartidores = repartidores;
        this.establecimientos = establecimientos;
        this.estrategias = estrategias;
        this.bitacora = bitacora;
    }

    /** Para cada repartidor: sus pedidos activos y los productos (con cantidades) que lleva. */
    public List<CargaRepartidorDTO> productosPorRepartidor() {
        return repartidores.findAll().stream().map(this::carga).toList();
    }

    /** Para cada establecimiento: su inventario actual con alertas de stock bajo o agotado. */
    public List<DisponibilidadEstablecimientoDTO> disponibilidadPorEstablecimiento() {
        return establecimientos.findAll().stream().map(this::disponibilidad).toList();
    }

    public List<EventoPedido> bitacora() {
        return bitacora.ultimosEventos();
    }

    public EstrategiasDTO estrategias() {
        return estrategias.estado();
    }

    public EstrategiasDTO cambiarEstrategias(String asignacion, String tiempo) {
        estrategias.cambiar(asignacion, tiempo);
        return estrategias.estado();
    }

    private CargaRepartidorDTO carga(Repartidor repartidor) {
        List<PedidoEnRuta> pedidos = repartidor.getPedidosActivos().stream().map(this::pedidoEnRuta).toList();
        int unidades = repartidor.productosQueLleva().stream().mapToInt(ItemPedido::getCantidad).sum();
        return new CargaRepartidorDTO(repartidor.getId(), repartidor.getNombre(), repartidor.getVehiculo(),
                repartidor.isEnTurno(), UbicacionDTO.desde(repartidor.getUbicacionActual()), pedidos, unidades);
    }

    private PedidoEnRuta pedidoEnRuta(Pedido pedido) {
        List<ProductoCantidad> productos = pedido.getItems().stream()
                .map(item -> new ProductoCantidad(item.getProducto().getNombre(), item.getCantidad()))
                .toList();
        return new PedidoEnRuta(pedido.getId(), pedido.getCliente().getNombre(), pedido.getDireccionEntrega(),
                pedido.getEstado().name(), productos);
    }

    private DisponibilidadEstablecimientoDTO disponibilidad(Establecimiento establecimiento) {
        List<ItemInventario> items = establecimiento.getInventario().getItems();
        List<String> alertas = establecimiento.getInventario().itemsConAlerta().stream()
                .map(item -> item.nivel() == NivelStock.AGOTADO
                        ? item.getProducto().getNombre() + " está AGOTADO"
                        : item.getProducto().getNombre() + " tiene stock bajo (" + item.getCantidad()
                        + " de mínimo " + item.getStockMinimo() + ")")
                .toList();
        return new DisponibilidadEstablecimientoDTO(establecimiento.getId(), establecimiento.getNombre(),
                establecimiento.getDireccion(), items.stream().map(ItemInventarioDTO::desde).toList(), alertas);
    }
}
