package co.edu.ucc.domicilios.servicio;

import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Costo del domicilio = tarifa base + tarifa por kilómetro (redondeado a la centena). */
@Component
public class CalculadorCostoEnvio {

    private final double tarifaBase;
    private final double tarifaPorKm;

    public CalculadorCostoEnvio(@Value("${domicilios.envio.tarifa-base}") double tarifaBase,
                                @Value("${domicilios.envio.tarifa-por-km}") double tarifaPorKm) {
        this.tarifaBase = tarifaBase;
        this.tarifaPorKm = tarifaPorKm;
    }

    public double calcular(Ubicacion origen, Ubicacion destino) {
        double costo = tarifaBase + tarifaPorKm * origen.distanciaKmA(destino);
        return Math.round(costo / 100) * 100.0;
    }
}
