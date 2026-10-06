package co.edu.ucc.domicilios.patrones.asignacion;

import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;
import co.edu.ucc.domicilios.dominio.usuario.Repartidor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Escoge el repartidor disponible más cercano al establecimiento. */
@Component
public class AsignacionMasCercano implements EstrategiaAsignacion {

    public static final String NOMBRE = "mas-cercano";

    @Override
    public String nombre() {
        return NOMBRE;
    }

    @Override
    public Optional<Repartidor> seleccionar(List<Repartidor> disponibles, Ubicacion origen) {
        return disponibles.stream()
                .min(Comparator.comparingDouble(r -> r.getUbicacionActual().distanciaKmA(origen)));
    }
}
