package co.edu.ucc.domicilios.patrones.descuento;

/**
 * Decorador base: envuelve otro descuento y le resta una rebaja adicional.
 * Así varios descuentos se pueden apilar sobre un mismo pedido.
 */
public abstract class DescuentoDecorator implements Descuento {

    protected final Descuento envuelto;

    protected DescuentoDecorator(Descuento envuelto) {
        this.envuelto = envuelto;
    }

    @Override
    public final double aplicar(DetalleCosto costo) {
        double totalParcial = envuelto.aplicar(costo);
        return Math.max(0, totalParcial - calcularRebaja(costo, totalParcial));
    }

    @Override
    public final String descripcion() {
        String anteriores = envuelto.descripcion();
        return anteriores.isEmpty() ? nombre() : anteriores + " + " + nombre();
    }

    protected abstract double calcularRebaja(DetalleCosto costo, double totalParcial);

    protected abstract String nombre();
}
