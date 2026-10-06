package co.edu.ucc.domicilios.dominio.ubicacion;

import jakarta.persistence.Embeddable;

import java.util.Locale;
import java.util.Objects;

/** Coordenadas geográficas (objeto de valor inmutable). */
@Embeddable
public class Ubicacion {

    private static final double RADIO_TIERRA_KM = 6371.0;

    private double latitud;
    private double longitud;

    protected Ubicacion() {
    }

    public Ubicacion(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    /** Distancia en línea recta (fórmula de Haversine). */
    public double distanciaKmA(Ubicacion otra) {
        double dLat = Math.toRadians(otra.latitud - latitud);
        double dLon = Math.toRadians(otra.longitud - longitud);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(latitud)) * Math.cos(Math.toRadians(otra.latitud))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return RADIO_TIERRA_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ubicacion u)) return false;
        return Double.compare(latitud, u.latitud) == 0 && Double.compare(longitud, u.longitud) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitud, longitud);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "(%.5f, %.5f)", latitud, longitud);
    }
}
