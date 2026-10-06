package co.edu.ucc.domicilios.patrones.descuento;

/** Valores sobre los que se calculan los descuentos. */
public record DetalleCosto(double subtotal, double costoEnvio) {

    public double bruto() {
        return subtotal + costoEnvio;
    }
}
