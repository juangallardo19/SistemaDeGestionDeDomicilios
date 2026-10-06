package co.edu.ucc.domicilios.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Cuerpo de POST /api/pedidos y /api/pedidos/cotizacion. */
public record SolicitudPedido(
        @NotNull Long clienteId,
        @NotNull Long establecimientoId,
        @NotEmpty @Valid List<ItemSolicitud> items,
        @NotBlank String metodoPago,
        List<String> codigosDescuento) {
}
