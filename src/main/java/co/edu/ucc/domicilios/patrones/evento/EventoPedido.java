package co.edu.ucc.domicilios.patrones.evento;

import java.time.LocalDateTime;

/** Lo que el sujeto (PublicadorEventosPedido) envía a cada observador. */
public record EventoPedido(TipoEvento tipo, Long pedidoId, String cliente, String estado,
                           String mensaje, LocalDateTime fecha) {

    public static EventoPedido de(TipoEvento tipo, Long pedidoId, String cliente, String estado, String mensaje) {
        return new EventoPedido(tipo, pedidoId, cliente, estado, mensaje, LocalDateTime.now());
    }
}
