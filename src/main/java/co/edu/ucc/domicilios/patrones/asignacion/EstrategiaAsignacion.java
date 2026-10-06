package co.edu.ucc.domicilios.patrones.asignacion;

import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;
import co.edu.ucc.domicilios.dominio.usuario.Repartidor;

import java.util.List;
import java.util.Optional;

/** Patrón Strategy: criterio para escoger el repartidor de un pedido. */
public interface EstrategiaAsignacion {

    String nombre();

    Optional<Repartidor> seleccionar(List<Repartidor> disponibles, Ubicacion origen);
}
