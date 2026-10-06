package co.edu.ucc.domicilios.dto;

import java.util.List;

/** Lo que ve el cliente: posición del repartidor, ruta, distancia y tiempo estimado de llegada. */
public record SeguimientoDTO(Long pedidoId, String estado, String descripcionEstado,
                             String repartidor, String vehiculo,
                             UbicacionDTO posicionActual, UbicacionDTO origen, UbicacionDTO destino,
                             List<UbicacionDTO> recorrido, List<UbicacionDTO> restante,
                             double distanciaRestanteKm, int progreso, int minutosEstimados,
                             String calculadorTiempo) {
}
