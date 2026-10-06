package co.edu.ucc.domicilios.patrones.evento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/** Observador para el administrador: registra los últimos eventos de todos los pedidos. */
@Component
public class BitacoraPedidos implements ObservadorPedido {

    private static final Logger log = LoggerFactory.getLogger(BitacoraPedidos.class);
    private static final int MAXIMO_EVENTOS = 100;

    private final Deque<EventoPedido> eventos = new ArrayDeque<>();

    @Override
    public synchronized void actualizar(EventoPedido evento) {
        if (evento.tipo() == TipoEvento.ESTADO_CAMBIADO) {
            log.info("Pedido #{} -> {}", evento.pedidoId(), evento.estado());
        }
        eventos.addFirst(evento);
        if (eventos.size() > MAXIMO_EVENTOS) {
            eventos.removeLast();
        }
    }

    public synchronized List<EventoPedido> ultimosEventos() {
        return List.copyOf(eventos);
    }
}
