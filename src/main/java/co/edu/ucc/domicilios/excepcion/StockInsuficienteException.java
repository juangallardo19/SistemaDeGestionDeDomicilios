package co.edu.ucc.domicilios.excepcion;

public class StockInsuficienteException extends ReglaNegocioException {

    public StockInsuficienteException(String producto, int solicitado, int disponible) {
        super("Stock insuficiente de '" + producto + "': se pidieron " + solicitado
                + " y hay " + disponible + " disponibles");
    }
}
