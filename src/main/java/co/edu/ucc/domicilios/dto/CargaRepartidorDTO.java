package co.edu.ucc.domicilios.dto;

import java.util.List;

/** Consulta del administrador: productos que lleva cada domiciliario. */
public record CargaRepartidorDTO(Long repartidorId, String repartidor, String vehiculo, boolean enTurno,
                                 UbicacionDTO ubicacion, List<PedidoEnRuta> pedidosActivos, int totalUnidades) {

    public record PedidoEnRuta(Long pedidoId, String cliente, String direccion, String estado,
                               List<ProductoCantidad> productos) {
    }

    public record ProductoCantidad(String producto, int cantidad) {
    }
}
