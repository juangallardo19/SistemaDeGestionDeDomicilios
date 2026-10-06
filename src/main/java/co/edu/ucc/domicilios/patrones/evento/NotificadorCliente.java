package co.edu.ucc.domicilios.patrones.evento;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Observador que simula las notificaciones push al cliente.
 * Imprime el aviso y lo guarda para consultarlo en GET /api/pedidos/{id}/notificaciones.
 */
@Component
public class NotificadorCliente implements ObservadorPedido {

    private final Map<Long, List<String>> notificacionesPorPedido = new ConcurrentHashMap<>();
    private volatile boolean imprimirEnConsola = true;

    @Override
    public void actualizar(EventoPedido evento) {
        String mensaje = "Hola " + evento.cliente() + ", tu pedido #" + evento.pedidoId() + ": " + evento.mensaje();
        notificacionesPorPedido.computeIfAbsent(evento.pedidoId(), id -> new ArrayList<>()).add(mensaje);
        if (imprimirEnConsola) {
            System.out.println("   [Notificación al cliente] " + mensaje);
        }
    }

    public List<String> notificacionesDe(Long pedidoId) {
        return List.copyOf(notificacionesPorPedido.getOrDefault(pedidoId, List.of()));
    }

    public void setImprimirEnConsola(boolean imprimirEnConsola) {
        this.imprimirEnConsola = imprimirEnConsola;
    }
}
