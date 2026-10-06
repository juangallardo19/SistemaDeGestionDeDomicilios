package co.edu.ucc.domicilios.patrones.descuento;

/** Valor fijo de descuento para la primera compra del cliente. */
public class DescuentoPrimeraCompra extends DescuentoDecorator {

    private final double valor;

    public DescuentoPrimeraCompra(Descuento envuelto, double valor) {
        super(envuelto);
        this.valor = valor;
    }

    @Override
    protected double calcularRebaja(DetalleCosto costo, double totalParcial) {
        return valor;
    }

    @Override
    protected String nombre() {
        return "Primera compra (-$" + (long) valor + ")";
    }
}
