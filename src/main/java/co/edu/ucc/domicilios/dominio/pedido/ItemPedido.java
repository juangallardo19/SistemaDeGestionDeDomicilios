package co.edu.ucc.domicilios.dominio.pedido;

import co.edu.ucc.domicilios.dominio.establecimiento.Producto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Línea de un pedido: producto, cantidad y precio al momento de pedir. */
@Entity
@Table(name = "items_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Pedido pedido;

    @ManyToOne(optional = false)
    private Producto producto;

    private int cantidad;

    private double precioUnitario;

    protected ItemPedido() {
    }

    ItemPedido(Pedido pedido, Producto producto, int cantidad) {
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
    }

    void sumarCantidad(int unidades) {
        cantidad += unidades;
    }

    public double subtotal() {
        return precioUnitario * cantidad;
    }

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }
}
