package co.edu.ucc.domicilios.dominio.establecimiento;

import co.edu.ucc.domicilios.excepcion.StockInsuficienteException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Cantidad disponible de un producto dentro del inventario de un establecimiento. */
@Entity
@Table(name = "items_inventario")
public class ItemInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Producto producto;

    private int cantidad;

    private int stockMinimo;

    protected ItemInventario() {
    }

    ItemInventario(Producto producto, int cantidad, int stockMinimo) {
        if (cantidad < 0 || stockMinimo < 0) {
            throw new IllegalArgumentException("Las cantidades no pueden ser negativas");
        }
        this.producto = producto;
        this.cantidad = cantidad;
        this.stockMinimo = stockMinimo;
    }

    public boolean alcanzaPara(int solicitado) {
        return cantidad >= solicitado;
    }

    /** Descuenta existencias; nunca deja el inventario en negativo. */
    void descontar(int unidades) {
        if (!alcanzaPara(unidades)) {
            throw new StockInsuficienteException(producto.getNombre(), unidades, cantidad);
        }
        cantidad -= unidades;
    }

    void ajustar(int nuevaCantidad) {
        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        cantidad = nuevaCantidad;
    }

    public NivelStock nivel() {
        if (cantidad == 0) {
            return NivelStock.AGOTADO;
        }
        return cantidad <= stockMinimo ? NivelStock.BAJO : NivelStock.DISPONIBLE;
    }

    public Long getId() {
        return id;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }
}
