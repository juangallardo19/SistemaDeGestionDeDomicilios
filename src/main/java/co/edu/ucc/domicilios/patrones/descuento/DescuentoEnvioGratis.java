package co.edu.ucc.domicilios.patrones.descuento;

/** Descuenta el costo del domicilio. */
public class DescuentoEnvioGratis extends DescuentoDecorator {

    public DescuentoEnvioGratis(Descuento envuelto) {
        super(envuelto);
    }

    @Override
    protected double calcularRebaja(DetalleCosto costo, double totalParcial) {
        return costo.costoEnvio();
    }

    @Override
    protected String nombre() {
        return "Envío gratis";
    }
}
