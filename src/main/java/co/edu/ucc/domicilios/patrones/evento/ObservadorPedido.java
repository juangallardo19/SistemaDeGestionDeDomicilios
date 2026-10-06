package co.edu.ucc.domicilios.patrones.evento;

/** Patrón Observer: quien quiera enterarse de los cambios de un pedido implementa esta interfaz. */
public interface ObservadorPedido {

    void actualizar(EventoPedido evento);
}
