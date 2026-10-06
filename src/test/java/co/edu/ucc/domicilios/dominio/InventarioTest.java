package co.edu.ucc.domicilios.dominio;

import co.edu.ucc.domicilios.dominio.establecimiento.Categoria;
import co.edu.ucc.domicilios.dominio.establecimiento.Establecimiento;
import co.edu.ucc.domicilios.dominio.establecimiento.Inventario;
import co.edu.ucc.domicilios.dominio.establecimiento.NivelStock;
import co.edu.ucc.domicilios.dominio.establecimiento.Producto;
import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;
import co.edu.ucc.domicilios.excepcion.StockInsuficienteException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventarioTest {

    private final Establecimiento establecimiento = new Establecimiento("Café", "Calle 79", new Ubicacion(4.66, -74.05));
    private final Producto cafe = establecimiento.agregarProducto("Café", 5000, Categoria.BEBIDA, 10, 3);
    private final Producto croissant = establecimiento.agregarProducto("Croissant", 8000, Categoria.COMIDA, 2, 3);
    private final Inventario inventario = establecimiento.getInventario();

    @Test
    void descuentaExistencias() {
        inventario.descontar(Map.of(cafe, 4));
        assertThat(inventario.cantidadDisponible(cafe)).isEqualTo(6);
    }

    @Test
    void noDescuentaNadaSiUnProductoNoAlcanza() {
        assertThatThrownBy(() -> inventario.descontar(Map.of(cafe, 2, croissant, 5)))
                .isInstanceOf(StockInsuficienteException.class);
        assertThat(inventario.cantidadDisponible(cafe)).isEqualTo(10);
        assertThat(inventario.cantidadDisponible(croissant)).isEqualTo(2);
    }

    @Test
    void avisaStockBajoYAgotado() {
        assertThat(inventario.itemDe(croissant).nivel()).isEqualTo(NivelStock.BAJO);
        inventario.descontar(Map.of(croissant, 2));
        assertThat(inventario.itemDe(croissant).nivel()).isEqualTo(NivelStock.AGOTADO);
        assertThat(inventario.itemsConAlerta()).hasSize(1);
    }
}
