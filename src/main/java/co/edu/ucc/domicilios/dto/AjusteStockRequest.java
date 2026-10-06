package co.edu.ucc.domicilios.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record AjusteStockRequest(@PositiveOrZero int cantidad) {
}
