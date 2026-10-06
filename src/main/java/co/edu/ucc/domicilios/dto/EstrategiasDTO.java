package co.edu.ucc.domicilios.dto;

import java.util.Set;

public record EstrategiasDTO(String asignacion, String tiempo,
                             Set<String> opcionesAsignacion, Set<String> opcionesTiempo) {
}
