package co.edu.ucc.domicilios.patrones.pago;

import org.springframework.stereotype.Component;

import java.util.UUID;

/** Pago con tarjeta simulado: rechaza montos que superen el cupo de la tarjeta. */
@Component
public class PagoTarjeta implements MetodoPago {

    private static final double CUPO_MAXIMO = 1_500_000;

    @Override
    public String codigo() {
        return "TARJETA";
    }

    @Override
    public ResultadoPago pagar(double monto) {
        if (monto > CUPO_MAXIMO) {
            return ResultadoPago.rechazado("el monto supera el cupo disponible de la tarjeta");
        }
        return ResultadoPago.aprobado("TAR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "Pago con tarjeta aprobado por $" + (long) monto);
    }
}
