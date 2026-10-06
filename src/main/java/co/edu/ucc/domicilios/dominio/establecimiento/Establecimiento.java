package co.edu.ucc.domicilios.dominio.establecimiento;

import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Restaurante o tienda desde donde sale el domicilio. Tiene un catálogo y un inventario propio. */
@Entity
@Table(name = "establecimientos")
public class Establecimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String direccion;

    @Embedded
    private Ubicacion ubicacion;

    @OneToMany(mappedBy = "establecimiento", cascade = CascadeType.ALL)
    private List<Producto> catalogo = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "inventario_id")
    private Inventario inventario;

    protected Establecimiento() {
    }

    public Establecimiento(String nombre, String direccion, Ubicacion ubicacion) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.ubicacion = ubicacion;
        this.inventario = new Inventario();
    }

    /** Agrega un producto al catálogo y registra sus existencias iniciales en el inventario. */
    public Producto agregarProducto(String nombre, double precio, Categoria categoria,
                                    int existencias, int stockMinimo) {
        Producto producto = new Producto(nombre, precio, categoria, this);
        catalogo.add(producto);
        inventario.registrar(producto, existencias, stockMinimo);
        return producto;
    }

    public boolean ofrece(Producto producto) {
        return producto.getEstablecimiento() == this
                || (id != null && id.equals(producto.getEstablecimiento().getId()));
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public List<Producto> getCatalogo() {
        return Collections.unmodifiableList(catalogo);
    }

    public Inventario getInventario() {
        return inventario;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
