package co.edu.ucc.domicilios.patrones.asignacion;

import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;
import co.edu.ucc.domicilios.dominio.usuario.Repartidor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Reparte la carga: escoge al que menos entregas lleva; si empatan, al más cercano. */
@Component
public class AsignacionMenosOcupado implements EstrategiaAsignacion {

    public static final String NOMBRE = "menos-ocupado";

    @Override
    public String nombre() {
        return NOMBRE;
    }

    @Override
    public Optional<Repartidor> seleccionar(List<Repartidor> disponibles, Ubicacion origen) {
        return disponibles.stream()
                .min(Comparator.comparingInt(Repartidor::getEntregasRealizadas)
                        .thenComparingDouble(r -> r.getUbicacionActual().distanciaKmA(origen)));
    }
}
