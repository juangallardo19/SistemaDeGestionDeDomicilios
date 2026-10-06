package co.edu.ucc.domicilios.dto;

import co.edu.ucc.domicilios.dominio.establecimiento.Categoria;
import co.edu.ucc.domicilios.dominio.establecimiento.ItemInventario;
import co.edu.ucc.domicilios.dominio.establecimiento.NivelStock;

public record ProductoDisponibleDTO(Long id, String nombre, Categoria categoria, double precio,
                                    int disponibles, NivelStock nivel) {

    public static ProductoDisponibleDTO desde(ItemInventario item) {
        return new ProductoDisponibleDTO(item.getProducto().getId(), item.getProducto().getNombre(),
                item.getProducto().getCategoria(), item.getProducto().getPrecio(),
                item.getCantidad(), item.nivel());
    }
}
