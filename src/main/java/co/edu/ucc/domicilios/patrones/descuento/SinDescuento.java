package co.edu.ucc.domicilios.patrones.descuento;

/** Componente base: cobra el total sin rebajas. Los decoradores se apilan sobre él. */
public final class SinDescuento implements Descuento {

    @Override
    public double aplicar(DetalleCosto costo) {
        return costo.bruto();
    }

    @Override
    public String descripcion() {
        return "";
    }
}
