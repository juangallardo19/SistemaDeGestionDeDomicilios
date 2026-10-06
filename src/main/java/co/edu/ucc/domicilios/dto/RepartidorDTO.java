package co.edu.ucc.domicilios.dto;

import co.edu.ucc.domicilios.dominio.usuario.Repartidor;

public record RepartidorDTO(Long id, String nombre, String vehiculo, boolean enTurno, boolean disponible,
                            int entregasRealizadas, UbicacionDTO ubicacion) {

    public static RepartidorDTO desde(Repartidor r) {
        return new RepartidorDTO(r.getId(), r.getNombre(), r.getVehiculo(), r.isEnTurno(), r.estaDisponible(),
                r.getEntregasRealizadas(), UbicacionDTO.desde(r.getUbicacionActual()));
    }
}
