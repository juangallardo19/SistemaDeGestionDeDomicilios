package co.edu.ucc.domicilios.servicio;

import co.edu.ucc.domicilios.dto.EstrategiasDTO;
import co.edu.ucc.domicilios.excepcion.ReglaNegocioException;
import co.edu.ucc.domicilios.patrones.asignacion.EstrategiaAsignacion;
import co.edu.ucc.domicilios.patrones.tiempo.CalculadorTiempo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Guarda qué estrategia de asignación y de cálculo de tiempo está activa.
 * Spring inyecta todas las implementaciones; el administrador puede cambiarlas en caliente.
 */
@Component
public class ConfiguracionEstrategias {

    private final Map<String, EstrategiaAsignacion> estrategiasAsignacion;
    private final Map<String, CalculadorTiempo> calculadoresTiempo;
    private volatile EstrategiaAsignacion asignacionActual;
    private volatile CalculadorTiempo tiempoActual;

    public ConfiguracionEstrategias(List<EstrategiaAsignacion> asignaciones,
                                    List<CalculadorTiempo> calculadores,
                                    @Value("${domicilios.asignacion.estrategia}") String asignacionInicial,
                                    @Value("${domicilios.tiempo.estrategia}") String tiempoInicial) {
        this.estrategiasAsignacion = asignaciones.stream()
                .collect(Collectors.toMap(EstrategiaAsignacion::nombre, Function.identity(), (a, b) -> a, TreeMap::new));
        this.calculadoresTiempo = calculadores.stream()
                .collect(Collectors.toMap(CalculadorTiempo::nombre, Function.identity(), (a, b) -> a, TreeMap::new));
        cambiar(asignacionInicial, tiempoInicial);
    }

    public EstrategiaAsignacion asignacion() {
        return asignacionActual;
    }

    public CalculadorTiempo calculadorTiempo() {
        return tiempoActual;
    }

    public CalculadorTiempo calculadorTiempo(String nombre) {
        return buscar(calculadoresTiempo, nombre, "cálculo de tiempo");
    }

    public Collection<CalculadorTiempo> calculadoresDisponibles() {
        return calculadoresTiempo.values();
    }

    public void cambiar(String asignacion, String tiempo) {
        if (asignacion != null && !asignacion.isBlank()) {
            asignacionActual = buscar(estrategiasAsignacion, asignacion, "asignación");
        }
        if (tiempo != null && !tiempo.isBlank()) {
            tiempoActual = buscar(calculadoresTiempo, tiempo, "cálculo de tiempo");
        }
    }

    public EstrategiasDTO estado() {
        return new EstrategiasDTO(asignacionActual.nombre(), tiempoActual.nombre(),
                estrategiasAsignacion.keySet(), calculadoresTiempo.keySet());
    }

    private static <T> T buscar(Map<String, T> opciones, String nombre, String tipo) {
        T encontrada = opciones.get(nombre.trim().toLowerCase());
        if (encontrada == null) {
            throw new ReglaNegocioException("Estrategia de " + tipo + " desconocida: " + nombre
                    + ". Opciones: " + opciones.keySet());
        }
        return encontrada;
    }
}
