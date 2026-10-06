package co.edu.ucc.domicilios.dto;

import co.edu.ucc.domicilios.dominio.establecimiento.Establecimiento;

public record EstablecimientoDTO(Long id, String nombre, String direccion, UbicacionDTO ubicacion, int productos) {

    public static EstablecimientoDTO desde(Establecimiento e) {
        return new EstablecimientoDTO(e.getId(), e.getNombre(), e.getDireccion(),
                UbicacionDTO.desde(e.getUbicacion()), e.getCatalogo().size());
    }
}
