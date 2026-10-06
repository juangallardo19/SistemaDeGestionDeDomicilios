package co.edu.ucc.domicilios.patrones.tiempo;

import co.edu.ucc.domicilios.dominio.ubicacion.Ruta;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalTime;

/** Igual que la velocidad promedio, pero más lento en horas pico (6-9 a.m. y 5-8 p.m.). */
@Component
public class CalculadorConTrafico implements CalculadorTiempo {

    public static final String NOMBRE = "con-trafico";
    private static final double FACTOR_HORA_PICO = 1.6;
    private static final double FACTOR_NORMAL = 1.15;

    private final Clock reloj;

    public CalculadorConTrafico() {
        this(Clock.systemDefaultZone());
    }

    CalculadorConTrafico(Clock reloj) {
        this.reloj = reloj;
    }

    @Override
    public String nombre() {
        return NOMBRE;
    }

    @Override
    public int minutosEstimados(Ruta ruta, double velocidadKmH) {
        if (ruta.haLlegado()) {
            return 0;
        }
        double minutosSinTrafico = ruta.distanciaRestanteKm() / velocidadKmH * 60;
        return (int) Math.ceil(minutosSinTrafico * factorActual());
    }

    double factorActual() {
        int hora = LocalTime.now(reloj).getHour();
        boolean horaPico = (hora >= 6 && hora < 9) || (hora >= 17 && hora < 20);
        return horaPico ? FACTOR_HORA_PICO : FACTOR_NORMAL;
    }
}
