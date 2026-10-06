package co.edu.ucc.domicilios.patrones.descuento;

/** Cupón con un porcentaje de descuento sobre el valor de los productos. */
public class DescuentoCupon extends DescuentoDecorator {

    private final String codigo;
    private final double porcentaje;

    public DescuentoCupon(Descuento envuelto, String codigo, double porcentaje) {
        super(envuelto);
        if (porcentaje <= 0 || porcentaje > 100) {
            throw new IllegalArgumentException("Porcentaje inválido: " + porcentaje);
        }
        this.codigo = codigo;
        this.porcentaje = porcentaje;
    }

    @Override
    protected double calcularRebaja(DetalleCosto costo, double totalParcial) {
        return costo.subtotal() * porcentaje / 100;
    }

    @Override
    protected String nombre() {
        return "Cupón " + codigo + " (-" + (int) porcentaje + "%)";
    }
}
