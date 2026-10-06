package co.edu.ucc.domicilios.dto;

import co.edu.ucc.domicilios.dominio.establecimiento.ItemInventario;
import co.edu.ucc.domicilios.dominio.establecimiento.NivelStock;

public record ItemInventarioDTO(Long productoId, String producto, String categoria, int cantidad,
                                int stockMinimo, NivelStock nivel) {

    public static ItemInventarioDTO desde(ItemInventario item) {
        return new ItemInventarioDTO(item.getProducto().getId(), item.getProducto().getNombre(),
                item.getProducto().getCategoria().name(), item.getCantidad(), item.getStockMinimo(), item.nivel());
    }
}
