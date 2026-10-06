package co.edu.ucc.domicilios.dto;

import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;

public record UbicacionDTO(double latitud, double longitud) {

    public static UbicacionDTO desde(Ubicacion ubicacion) {
        return ubicacion == null ? null : new UbicacionDTO(ubicacion.getLatitud(), ubicacion.getLongitud());
    }

    public Ubicacion aUbicacion() {
        return new Ubicacion(latitud, longitud);
    }
}
