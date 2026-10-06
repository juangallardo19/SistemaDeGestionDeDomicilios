package co.edu.ucc.domicilios.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemSolicitud(@NotNull Long productoId, @Positive int cantidad) {
}
