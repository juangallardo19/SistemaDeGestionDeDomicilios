package co.edu.ucc.domicilios.servicio;

import co.edu.ucc.domicilios.dominio.pedido.Pedido;
import co.edu.ucc.domicilios.dominio.ubicacion.Ruta;
import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;
import co.edu.ucc.domicilios.dominio.usuario.Repartidor;
import co.edu.ucc.domicilios.dto.SeguimientoDTO;
import co.edu.ucc.domicilios.dto.UbicacionDTO;
import co.edu.ucc.domicilios.excepcion.RecursoNoEncontradoException;
import co.edu.ucc.domicilios.patrones.tiempo.CalculadorTiempo;
import co.edu.ucc.domicilios.repositorio.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Entrega al cliente la ubicación del repartidor, la ruta y el tiempo estimado de llegada. */
@Service
@Transactional(readOnly = true)
public class ServicioSeguimiento {

    private final PedidoRepository pedidos;
    private final ConfiguracionEstrategias estrategias;

    public ServicioSeguimiento(PedidoRepository pedidos, ConfiguracionEstrategias estrategias) {
        this.pedidos = pedidos;
        this.estrategias = estrategias;
    }

    public SeguimientoDTO seguimiento(Long pedidoId) {
        Pedido pedido = pedidos.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", pedidoId));
        return armar(pedido, estrategias.calculadorTiempo());
    }

    /** Tiempo estimado del pedido con cada estrategia disponible (para compararlas). */
    public Map<String, Integer> estimacionesPorEstrategia(Long pedidoId) {
        Pedido pedido = pedidos.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", pedidoId));
        Map<String, Integer> estimaciones = new LinkedHashMap<>();
        estrategias.calculadoresDisponibles()
                .forEach(calculador -> estimaciones.put(calculador.nombre(), minutosEstimados(pedido, calculador)));
        return estimaciones;
    }

    private int minutosEstimados(Pedido pedido, CalculadorTiempo calculador) {
        Ruta ruta = pedido.getRuta();
        return ruta == null ? 0 : calculador.minutosEstimados(ruta, pedido.getRepartidor().getVelocidadPromedioKmH());
    }

    SeguimientoDTO armar(Pedido pedido, CalculadorTiempo calculador) {
        Ruta ruta = pedido.getRuta();
        Repartidor repartidor = pedido.getRepartidor();
        if (ruta == null || repartidor == null) {
            return new SeguimientoDTO(pedido.getId(), pedido.getEstado().name(),
                    pedido.getEstadoActual().descripcion(), null, null, null,
                    UbicacionDTO.desde(pedido.getEstablecimiento().getUbicacion()),
                    UbicacionDTO.desde(pedido.getDestino()), List.of(), List.of(), 0, 0, 0, calculador.nombre());
        }
        List<Ubicacion> puntos = ruta.getPuntos();
        int actual = ruta.getIndiceActual();
        return new SeguimientoDTO(pedido.getId(), pedido.getEstado().name(), pedido.getEstadoActual().descripcion(),
                repartidor.getNombre(), repartidor.getVehiculo(),
                UbicacionDTO.desde(ruta.posicionActual()), UbicacionDTO.desde(ruta.origen()),
                UbicacionDTO.desde(ruta.destino()),
                puntos.subList(0, actual + 1).stream().map(UbicacionDTO::desde).toList(),
                puntos.subList(actual, puntos.size()).stream().map(UbicacionDTO::desde).toList(),
                Math.round(ruta.distanciaRestanteKm() * 100) / 100.0, ruta.progreso(),
                minutosEstimados(pedido, calculador), calculador.nombre());
    }
}
