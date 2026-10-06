package co.edu.ucc.domicilios.patrones.estado;

/** Valor que se guarda en la base de datos; cada uno conoce su objeto de comportamiento. */
public enum EstadoPedidoTipo {
    RECIBIDO,
    PREPARANDO,
    EN_CAMINO,
    ENTREGADO,
    CANCELADO;

    public EstadoPedido comportamiento() {
        return switch (this) {
            case RECIBIDO -> EstadoRecibido.INSTANCIA;
            case PREPARANDO -> EstadoPreparando.INSTANCIA;
            case EN_CAMINO -> EstadoEnCamino.INSTANCIA;
            case ENTREGADO -> EstadoEntregado.INSTANCIA;
            case CANCELADO -> EstadoCancelado.INSTANCIA;
        };
    }
}
