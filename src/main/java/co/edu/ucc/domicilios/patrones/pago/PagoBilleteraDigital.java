package co.edu.ucc.domicilios.patrones.pago;

import org.springframework.stereotype.Component;

import java.util.UUID;

/** Billetera digital simulada (Nequi, Daviplata...): tiene un tope por transacción. */
@Component
public class PagoBilleteraDigital implements MetodoPago {

    private static final double TOPE_TRANSACCION = 500_000;

    @Override
    public String codigo() {
        return "BILLETERA";
    }

    @Override
    public ResultadoPago pagar(double monto) {
        if (monto > TOPE_TRANSACCION) {
            return ResultadoPago.rechazado("la billetera digital no permite transacciones mayores a $" + (long) TOPE_TRANSACCION);
        }
        return ResultadoPago.aprobado("BIL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "Pago con billetera digital aprobado por $" + (long) monto);
    }
}
