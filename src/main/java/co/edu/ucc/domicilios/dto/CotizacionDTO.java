package co.edu.ucc.domicilios.dto;

public record CotizacionDTO(double subtotal, double costoEnvio, double descuento, double total,
                            String descuentosAplicados, double distanciaKm) {
}
