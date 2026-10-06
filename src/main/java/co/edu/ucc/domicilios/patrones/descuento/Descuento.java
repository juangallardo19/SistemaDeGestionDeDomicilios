package co.edu.ucc.domicilios.patrones.descuento;

/** Contrato para aplicar descuentos al total de un pedido (componente del patrón Decorator). */
public interface Descuento {

    /** Devuelve el total a pagar después de aplicar este descuento. */
    double aplicar(DetalleCosto costo);

    String descripcion();
}
