package co.edu.ucc.domicilios.patrones.pago;

/**
 * Contrato para pagar un pedido. Agregar un método nuevo (por ejemplo PSE)
 * es crear otra clase con @Component: no se modifica ninguna existente.
 */
public interface MetodoPago {

    /** Código con el que el cliente elige el método: EFECTIVO, TARJETA, BILLETERA... */
    String codigo();

    ResultadoPago pagar(double monto);
}
