package co.edu.ucc.domicilios.dto;

import co.edu.ucc.domicilios.dominio.pedido.Pedido;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoDTO(Long id, String cliente, String establecimiento, String repartidor,
                        String estado, String descripcionEstado, List<ItemPedidoDTO> items,
                        double subtotal, double costoEnvio, double descuento, double total,
                        String metodoPago, String referenciaPago, String descuentosAplicados,
                        String direccionEntrega, LocalDateTime creadoEn, LocalDateTime entregadoEn) {

    public static PedidoDTO desde(Pedido p) {
        return new PedidoDTO(p.getId(), p.getCliente().getNombre(), p.getEstablecimiento().getNombre(),
                p.getRepartidor() == null ? null : p.getRepartidor().getNombre(),
                p.getEstado().name(), p.getEstadoActual().descripcion(),
                p.getItems().stream().map(ItemPedidoDTO::desde).toList(),
                p.getSubtotal(), p.getCostoEnvio(), p.getDescuento(), p.getTotal(),
                p.getMetodoPago(), p.getReferenciaPago(), p.getDescuentosAplicados(),
                p.getDireccionEntrega(), p.getCreadoEn(), p.getEntregadoEn());
    }
}
