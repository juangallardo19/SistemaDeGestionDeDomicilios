package co.edu.ucc.domicilios.patrones.estado;

import co.edu.ucc.domicilios.excepcion.TransicionInvalidaException;

/**
 * Patrón State: cada estado del pedido sabe a qué estado puede pasar.
 * Por defecto toda transición es inválida; cada estado concreto sobrescribe
 * solo las transiciones que permite (Segregación + Abierto/Cerrado).
 */
public interface EstadoPedido {

    EstadoPedidoTipo tipo();

    String descripcion();

    /** Un pedido activo todavía no se ha entregado ni cancelado. */
    boolean esActivo();

    default EstadoPedido preparar() {
        throw new TransicionInvalidaException(tipo().name(), "preparar");
    }

    default EstadoPedido despachar() {
        throw new TransicionInvalidaException(tipo().name(), "despachar");
    }

    default EstadoPedido entregar() {
        throw new TransicionInvalidaException(tipo().name(), "entregar");
    }

    default EstadoPedido cancelar() {
        throw new TransicionInvalidaException(tipo().name(), "cancelar");
    }
}
