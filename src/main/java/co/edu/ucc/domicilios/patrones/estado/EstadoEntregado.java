package co.edu.ucc.domicilios.patrones.estado;

public final class EstadoEntregado implements EstadoPedido {

    static final EstadoEntregado INSTANCIA = new EstadoEntregado();

    private EstadoEntregado() {
    }

    @Override
    public EstadoPedidoTipo tipo() {
        return EstadoPedidoTipo.ENTREGADO;
    }

    @Override
    public String descripcion() {
        return "Pedido entregado";
    }

    @Override
    public boolean esActivo() {
        return false;
    }
}
