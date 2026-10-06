package co.edu.ucc.domicilios.patrones.estado;

public final class EstadoCancelado implements EstadoPedido {

    static final EstadoCancelado INSTANCIA = new EstadoCancelado();

    private EstadoCancelado() {
    }

    @Override
    public EstadoPedidoTipo tipo() {
        return EstadoPedidoTipo.CANCELADO;
    }

    @Override
    public String descripcion() {
        return "Pedido cancelado";
    }

    @Override
    public boolean esActivo() {
        return false;
    }
}
