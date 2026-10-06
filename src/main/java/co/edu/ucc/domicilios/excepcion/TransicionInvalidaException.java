package co.edu.ucc.domicilios.excepcion;

public class TransicionInvalidaException extends ReglaNegocioException {

    public TransicionInvalidaException(String estadoActual, String accion) {
        super("No se puede " + accion + " un pedido en estado " + estadoActual);
    }
}
