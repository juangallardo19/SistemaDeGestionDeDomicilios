package co.edu.ucc.domicilios.dto;

import co.edu.ucc.domicilios.dominio.pedido.ItemPedido;

public record ItemPedidoDTO(String producto, int cantidad, double precioUnitario, double subtotal) {

    public static ItemPedidoDTO desde(ItemPedido item) {
        return new ItemPedidoDTO(item.getProducto().getNombre(), item.getCantidad(),
                item.getPrecioUnitario(), item.subtotal());
    }
}
