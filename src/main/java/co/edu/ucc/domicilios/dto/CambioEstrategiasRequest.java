package co.edu.ucc.domicilios.dto;

/** Cualquiera de los dos campos puede ir vacío para dejar la estrategia actual. */
public record CambioEstrategiasRequest(String asignacion, String tiempo) {
}
