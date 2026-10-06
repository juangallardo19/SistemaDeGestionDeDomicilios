package co.edu.ucc.domicilios.dominio.establecimiento;

import co.edu.ucc.domicilios.excepcion.ReglaNegocioException;
import co.edu.ucc.domicilios.excepcion.StockInsuficienteException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Existencias de un establecimiento. Es la única clase que puede cambiar
 * las cantidades (encapsulamiento): descuenta, ajusta y avisa de stock bajo.
 */
@Entity
@Table(name = "inventarios")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inventario_id")
    private List<ItemInventario> items = new ArrayList<>();

    public void registrar(Producto producto, int cantidad, int stockMinimo) {
        if (buscarItem(producto) != null) {
            throw new ReglaNegocioException(producto.getNombre() + " ya está en el inventario");
        }
        items.add(new ItemInventario(producto, cantidad, stockMinimo));
    }

    public int cantidadDisponible(Producto producto) {
        ItemInventario item = buscarItem(producto);
        return item == null ? 0 : item.getCantidad();
    }

    public boolean hayDisponibilidad(Producto producto, int cantidad) {
        return cantidadDisponible(producto) >= cantidad;
    }

    /** Descuenta todas las cantidades o ninguna: primero valida y luego descuenta. */
    public void descontar(Map<Producto, Integer> cantidades) {
        cantidades.forEach((producto, cantidad) -> {
            if (!hayDisponibilidad(producto, cantidad)) {
                throw new StockInsuficienteException(producto.getNombre(), cantidad, cantidadDisponible(producto));
            }
        });
        cantidades.forEach((producto, cantidad) -> itemDe(producto).descontar(cantidad));
    }

    public void ajustar(Producto producto, int nuevaCantidad) {
        itemDe(producto).ajustar(nuevaCantidad);
    }

    public List<ItemInventario> itemsConAlerta() {
        return items.stream().filter(item -> item.nivel() != NivelStock.DISPONIBLE).toList();
    }

    public ItemInventario itemDe(Producto producto) {
        ItemInventario item = buscarItem(producto);
        if (item == null) {
            throw new ReglaNegocioException(producto.getNombre() + " no está en el inventario");
        }
        return item;
    }

    private ItemInventario buscarItem(Producto producto) {
        return items.stream()
                .filter(item -> item.getProducto() == producto
                        || (producto.getId() != null && Objects.equals(item.getProducto().getId(), producto.getId())))
                .findFirst()
                .orElse(null);
    }

    public Long getId() {
        return id;
    }

    public List<ItemInventario> getItems() {
        return Collections.unmodifiableList(items);
    }
}
