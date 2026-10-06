package co.edu.ucc.domicilios.patrones.pago;

import co.edu.ucc.domicilios.excepcion.ReglaNegocioException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Recibe todas las implementaciones de MetodoPago que Spring encuentre y las busca por código. */
@Component
public class FabricaMetodosPago {

    private final Map<String, MetodoPago> metodos;

    public FabricaMetodosPago(List<MetodoPago> implementaciones) {
        this.metodos = implementaciones.stream()
                .collect(Collectors.toMap(MetodoPago::codigo, Function.identity()));
    }

    public MetodoPago obtener(String codigo) {
        MetodoPago metodo = codigo == null ? null : metodos.get(codigo.trim().toUpperCase(Locale.ROOT));
        if (metodo == null) {
            throw new ReglaNegocioException("Método de pago no soportado: " + codigo
                    + ". Opciones: " + metodos.keySet());
        }
        return metodo;
    }
}
