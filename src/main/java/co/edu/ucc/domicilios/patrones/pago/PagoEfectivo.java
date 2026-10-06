package co.edu.ucc.domicilios.patrones.pago;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PagoEfectivo implements MetodoPago {

    @Override
    public String codigo() {
        return "EFECTIVO";
    }

    @Override
    public ResultadoPago pagar(double monto) {
        return ResultadoPago.aprobado("EFE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "Pago contra entrega: el repartidor recibirá $" + (long) monto);
    }
}
