package co.edu.ucc.domicilios.patrones.tiempo;

import co.edu.ucc.domicilios.dominio.ubicacion.Ruta;

/** Patrón Strategy: forma de estimar el tiempo de llegada del repartidor. */
public interface CalculadorTiempo {

    String nombre();

    /** Minutos que faltan para llegar, según la distancia restante de la ruta. */
    int minutosEstimados(Ruta ruta, double velocidadKmH);
}
