package co.edu.ucc.domicilios.patrones.tiempo;

import co.edu.ucc.domicilios.dominio.ubicacion.Ruta;
import org.springframework.stereotype.Component;

/** tiempo = distancia restante / velocidad promedio del repartidor. */
@Component
public class CalculadorPorVelocidadPromedio implements CalculadorTiempo {

    public static final String NOMBRE = "velocidad-promedio";

    @Override
    public String nombre() {
        return NOMBRE;
    }

    @Override
    public int minutosEstimados(Ruta ruta, double velocidadKmH) {
        if (ruta.haLlegado()) {
            return 0;
        }
        return (int) Math.ceil(ruta.distanciaRestanteKm() / velocidadKmH * 60);
    }
}
