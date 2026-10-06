package co.edu.ucc.domicilios.patrones.estado;

public final class EstadoPreparando implements EstadoPedido {

    static final EstadoPreparando INSTANCIA = new EstadoPreparando();

    private EstadoPreparando() {
    }

    @Override
    public EstadoPedidoTipo tipo() {
        return EstadoPedidoTipo.PREPARANDO;
    }

    @Override
    public String descripcion() {
        return "El establecimiento está preparando el pedido";
    }

    @Override
    public boolean esActivo() {
        return true;
    }

    @Override
    public EstadoPedido despachar() {
        return EstadoEnCamino.INSTANCIA;
    }
}
