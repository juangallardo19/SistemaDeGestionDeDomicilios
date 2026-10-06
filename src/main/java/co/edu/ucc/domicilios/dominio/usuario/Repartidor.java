package co.edu.ucc.domicilios.dominio.usuario;

import co.edu.ucc.domicilios.dominio.pedido.ItemPedido;
import co.edu.ucc.domicilios.dominio.pedido.Pedido;
import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;
import co.edu.ucc.domicilios.excepcion.ReglaNegocioException;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Repartidor: guarda su ubicación actual y los pedidos que lleva.
 * En esta versión un repartidor lleva un pedido a la vez.
 */
@Entity
@Table(name = "repartidores")
public class Repartidor extends Usuario {

    private String vehiculo;

    private double velocidadPromedioKmH;

    private boolean enTurno;

    private int entregasRealizadas;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitud", column = @Column(name = "actual_latitud")),
            @AttributeOverride(name = "longitud", column = @Column(name = "actual_longitud"))
    })
    private Ubicacion ubicacionActual;

    @OneToMany(mappedBy = "repartidor")
    private List<Pedido> pedidos = new ArrayList<>();

    protected Repartidor() {
    }

    public Repartidor(String nombre, String correo, String telefono, String vehiculo,
                      double velocidadPromedioKmH, Ubicacion ubicacionActual) {
        super(nombre, correo, telefono);
        this.vehiculo = vehiculo;
        this.velocidadPromedioKmH = velocidadPromedioKmH;
        this.ubicacionActual = ubicacionActual;
        this.enTurno = true;
    }

    @Override
    public Rol getRol() {
        return Rol.REPARTIDOR;
    }

    /** Disponible = está en turno y no lleva pedidos activos. */
    public boolean estaDisponible() {
        return enTurno && getPedidosActivos().isEmpty();
    }

    /** Recibe un pedido asignado. Lo invoca {@link Pedido#despachar}. */
    public void recibirPedido(Pedido pedido) {
        if (!estaDisponible()) {
            throw new ReglaNegocioException("El repartidor " + getNombre() + " no está disponible");
        }
        pedidos.add(pedido);
    }

    public void actualizarUbicacion(Ubicacion nuevaUbicacion) {
        this.ubicacionActual = nuevaUbicacion;
    }

    public void registrarEntrega() {
        entregasRealizadas++;
    }

    public void iniciarTurno() {
        enTurno = true;
    }

    public void terminarTurno() {
        if (!getPedidosActivos().isEmpty()) {
            throw new ReglaNegocioException("No puede terminar el turno con pedidos pendientes por entregar");
        }
        enTurno = false;
    }

    public List<Pedido> getPedidosActivos() {
        return pedidos.stream().filter(Pedido::estaActivo).toList();
    }

    /** Productos (con cantidades) que lleva en este momento en sus pedidos activos. */
    public List<ItemPedido> productosQueLleva() {
        return getPedidosActivos().stream()
                .flatMap(pedido -> pedido.getItems().stream())
                .toList();
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public double getVelocidadPromedioKmH() {
        return velocidadPromedioKmH;
    }

    public boolean isEnTurno() {
        return enTurno;
    }

    public int getEntregasRealizadas() {
        return entregasRealizadas;
    }

    public Ubicacion getUbicacionActual() {
        return ubicacionActual;
    }
}
