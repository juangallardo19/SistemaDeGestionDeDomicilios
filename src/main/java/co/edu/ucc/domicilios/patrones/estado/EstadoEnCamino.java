package co.edu.ucc.domicilios.patrones.estado;

public final class EstadoEnCamino implements EstadoPedido {

    static final EstadoEnCamino INSTANCIA = new EstadoEnCamino();

    private EstadoEnCamino() {
    }

    @Override
    public EstadoPedidoTipo tipo() {
        return EstadoPedidoTipo.EN_CAMINO;
    }

    @Override
    public String descripcion() {
        return "El repartidor va en camino";
    }

    @Override
    public boolean esActivo() {
        return true;
    }

    @Override
    public EstadoPedido entregar() {
        return EstadoEntregado.INSTANCIA;
    }
}
