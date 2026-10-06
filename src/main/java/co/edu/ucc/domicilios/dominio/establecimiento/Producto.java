package co.edu.ucc.domicilios.dominio.establecimiento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private double precio;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @ManyToOne(optional = false)
    private Establecimiento establecimiento;

    protected Producto() {
    }

    Producto(String nombre, double precio, Categoria categoria, Establecimiento establecimiento) {
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser positivo");
        }
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.establecimiento = establecimiento;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Establecimiento getEstablecimiento() {
        return establecimiento;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
