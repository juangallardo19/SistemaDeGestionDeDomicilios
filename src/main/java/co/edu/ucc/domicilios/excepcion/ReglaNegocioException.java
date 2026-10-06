package co.edu.ucc.domicilios.excepcion;

/** Se lanza cuando una operación viola una regla del negocio. */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
