package co.edu.ucc.domicilios.patrones.pago;

public record ResultadoPago(boolean aprobado, String referencia, String mensaje) {

    public static ResultadoPago aprobado(String referencia, String mensaje) {
        return new ResultadoPago(true, referencia, mensaje);
    }

    public static ResultadoPago rechazado(String mensaje) {
        return new ResultadoPago(false, null, mensaje);
    }
}
