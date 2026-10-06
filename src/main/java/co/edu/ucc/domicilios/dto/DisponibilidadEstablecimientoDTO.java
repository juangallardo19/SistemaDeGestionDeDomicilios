package co.edu.ucc.domicilios.dto;

import java.util.List;

/** Consulta del administrador: inventario de un establecimiento con sus alertas. */
public record DisponibilidadEstablecimientoDTO(Long establecimientoId, String establecimiento, String direccion,
                                               List<ItemInventarioDTO> inventario, List<String> alertas) {
}
