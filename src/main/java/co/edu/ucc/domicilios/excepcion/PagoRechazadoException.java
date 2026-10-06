package co.edu.ucc.domicilios.excepcion;

public class PagoRechazadoException extends ReglaNegocioException {

    public PagoRechazadoException(String mensaje) {
        super("Pago rechazado: " + mensaje);
    }
}
