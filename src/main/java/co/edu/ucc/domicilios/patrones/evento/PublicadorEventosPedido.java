package co.edu.ucc.domicilios.patrones.evento;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Sujeto del patrón Observer: mantiene la lista de observadores y les avisa de cada evento. */
@Component
public class PublicadorEventosPedido {

    private final List<ObservadorPedido> observadores = new CopyOnWriteArrayList<>();

    public PublicadorEventosPedido(List<ObservadorPedido> observadoresIniciales) {
        observadores.addAll(observadoresIniciales);
    }

    public void suscribir(ObservadorPedido observador) {
        observadores.add(observador);
    }

    public void desuscribir(ObservadorPedido observador) {
        observadores.remove(observador);
    }

    public void publicar(EventoPedido evento) {
        observadores.forEach(observador -> observador.actualizar(evento));
    }
}
