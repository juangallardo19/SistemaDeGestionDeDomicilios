package co.edu.ucc.domicilios.patrones.estado;

public final class EstadoRecibido implements EstadoPedido {

    static final EstadoRecibido INSTANCIA = new EstadoRecibido();

    private EstadoRecibido() {
    }

    @Override
    public EstadoPedidoTipo tipo() {
        return EstadoPedidoTipo.RECIBIDO;
    }

    @Override
    public String descripcion() {
        return "El establecimiento recibió el pedido";
    }

    @Override
    public boolean esActivo() {
        return true;
    }

    @Override
    public EstadoPedido preparar() {
        return EstadoPreparando.INSTANCIA;
    }

    @Override
    public EstadoPedido cancelar() {
        return EstadoCancelado.INSTANCIA;
    }
}
