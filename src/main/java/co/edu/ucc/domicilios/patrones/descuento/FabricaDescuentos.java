package co.edu.ucc.domicilios.patrones.descuento;

import co.edu.ucc.domicilios.excepcion.ReglaNegocioException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Convierte los códigos que escribe el cliente en una cadena de decoradores.
 * Ejemplo: [BIENVENIDA, ENVIOGRATIS] -> EnvioGratis(PrimeraCompra(SinDescuento)).
 */
@Component
public class FabricaDescuentos {

    public static final String PRIMERA_COMPRA = "BIENVENIDA";
    public static final String ENVIO_GRATIS = "ENVIOGRATIS";
    private static final double VALOR_PRIMERA_COMPRA = 5000;
    private static final double MINIMO_ENVIO_GRATIS = 40000;

    public Descuento crear(List<String> codigos, DetalleCosto costo, boolean esPrimeraCompra) {
        Descuento descuento = new SinDescuento();
        if (codigos == null) {
            return descuento;
        }
        Set<String> normalizados = new LinkedHashSet<>();
        for (String codigo : codigos) {
            if (codigo != null && !codigo.isBlank()) {
                normalizados.add(codigo.trim().toUpperCase(Locale.ROOT));
            }
        }
        for (String codigo : normalizados) {
            descuento = decorar(descuento, codigo, costo, esPrimeraCompra);
        }
        return descuento;
    }

    private Descuento decorar(Descuento base, String codigo, DetalleCosto costo, boolean esPrimeraCompra) {
        switch (codigo) {
            case PRIMERA_COMPRA -> {
                if (!esPrimeraCompra) {
                    throw new ReglaNegocioException("El código BIENVENIDA solo aplica en la primera compra");
                }
                return new DescuentoPrimeraCompra(base, VALOR_PRIMERA_COMPRA);
            }
            case ENVIO_GRATIS -> {
                if (costo.subtotal() < MINIMO_ENVIO_GRATIS) {
                    throw new ReglaNegocioException("ENVIOGRATIS aplica para pedidos desde $" + (long) MINIMO_ENVIO_GRATIS);
                }
                return new DescuentoEnvioGratis(base);
            }
            case "DOMI10" -> {
                return new DescuentoCupon(base, codigo, 10);
            }
            case "DOMI20" -> {
                return new DescuentoCupon(base, codigo, 20);
            }
            default -> throw new ReglaNegocioException("El código de descuento '" + codigo + "' no existe");
        }
    }
}
