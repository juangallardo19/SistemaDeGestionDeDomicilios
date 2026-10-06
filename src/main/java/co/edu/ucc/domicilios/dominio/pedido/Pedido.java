package co.edu.ucc.domicilios.dominio.pedido;

import co.edu.ucc.domicilios.dominio.establecimiento.Establecimiento;
import co.edu.ucc.domicilios.dominio.establecimiento.Producto;
import co.edu.ucc.domicilios.dominio.ubicacion.Ruta;
import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;
import co.edu.ucc.domicilios.dominio.usuario.Cliente;
import co.edu.ucc.domicilios.dominio.usuario.Repartidor;
import co.edu.ucc.domicilios.excepcion.PagoRechazadoException;
import co.edu.ucc.domicilios.excepcion.ReglaNegocioException;
import co.edu.ucc.domicilios.patrones.descuento.Descuento;
import co.edu.ucc.domicilios.patrones.descuento.DetalleCosto;
import co.edu.ucc.domicilios.patrones.estado.EstadoPedido;
import co.edu.ucc.domicilios.patrones.estado.EstadoPedidoTipo;
import co.edu.ucc.domicilios.patrones.pago.MetodoPago;
import co.edu.ucc.domicilios.patrones.pago.ResultadoPago;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pedido de un cliente a un establecimiento.
 *
 * - Patrón State: el ciclo de vida se delega en {@link EstadoPedido}; el pedido no
 *   tiene condicionales sobre su estado, cada estado decide qué transición es válida.
 * - Inversión de dependencias: para liquidar y pagar depende de las interfaces
 *   {@link Descuento} y {@link MetodoPago}, nunca de implementaciones concretas.
 */
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Cliente cliente;

    @ManyToOne(optional = false)
    private Establecimiento establecimiento;

    @ManyToOne
    private Repartidor repartidor;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedidoTipo estado = EstadoPedidoTipo.RECIBIDO;

    private String direccionEntrega;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitud", column = @Column(name = "destino_latitud")),
            @AttributeOverride(name = "longitud", column = @Column(name = "destino_longitud"))
    })
    private Ubicacion destino;

    private double subtotal;
    private double costoEnvio;
    private double descuento;
    private double total;

    private String metodoPago;
    private String referenciaPago;
    private String descuentosAplicados;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Ruta ruta;

    private LocalDateTime creadoEn;
    private LocalDateTime entregadoEn;

    protected Pedido() {
    }

    public Pedido(Cliente cliente, Establecimiento establecimiento) {
        this.cliente = cliente;
        this.establecimiento = establecimiento;
        this.direccionEntrega = cliente.getDireccion();
        this.destino = cliente.getUbicacion();
        this.creadoEn = LocalDateTime.now();
    }

    // ------------------------------------------------------------ productos

    public void agregarItem(Producto producto, int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
        }
        if (!establecimiento.ofrece(producto)) {
            throw new ReglaNegocioException(producto.getNombre() + " no pertenece a " + establecimiento.getNombre());
        }
        items.stream()
                .filter(item -> item.getProducto() == producto)
                .findFirst()
                .ifPresentOrElse(item -> item.sumarCantidad(cantidad),
                        () -> items.add(new ItemPedido(this, producto, cantidad)));
    }

    public Map<Producto, Integer> cantidadesPorProducto() {
        Map<Producto, Integer> cantidades = new LinkedHashMap<>();
        items.forEach(item -> cantidades.merge(item.getProducto(), item.getCantidad(), Integer::sum));
        return cantidades;
    }

    public double calcularSubtotal() {
        return items.stream().mapToDouble(ItemPedido::subtotal).sum();
    }

    // ------------------------------------------------------------ costos y pago

    /** total = suma de productos + costo del domicilio - descuentos (Decorator). */
    public void liquidar(double costoEnvio, Descuento descuentoAplicable) {
        if (items.isEmpty()) {
            throw new ReglaNegocioException("El pedido no tiene productos");
        }
        this.subtotal = calcularSubtotal();
        this.costoEnvio = costoEnvio;
        this.total = Math.round(descuentoAplicable.aplicar(new DetalleCosto(subtotal, costoEnvio)));
        this.descuento = subtotal + costoEnvio - total;
        this.descuentosAplicados = descuentoAplicable.descripcion();
    }

    /** Polimorfismo: el pedido no sabe qué método de pago concreto recibe. */
    public ResultadoPago pagar(MetodoPago metodo) {
        ResultadoPago resultado = metodo.pagar(total);
        if (!resultado.aprobado()) {
            throw new PagoRechazadoException(resultado.mensaje());
        }
        this.metodoPago = metodo.codigo();
        this.referenciaPago = resultado.referencia();
        return resultado;
    }

    // ------------------------------------------------------------ ciclo de vida (State)

    public EstadoPedido getEstadoActual() {
        return estado.comportamiento();
    }

    public void iniciarPreparacion() {
        estado = getEstadoActual().preparar().tipo();
    }

    public void despachar(Repartidor asignado, Ruta rutaDeEntrega) {
        EstadoPedido siguiente = getEstadoActual().despachar();
        asignado.recibirPedido(this);
        this.repartidor = asignado;
        this.ruta = rutaDeEntrega;
        this.estado = siguiente.tipo();
    }

    public void marcarEntregado() {
        estado = getEstadoActual().entregar().tipo();
        entregadoEn = LocalDateTime.now();
        repartidor.actualizarUbicacion(destino);
        repartidor.registrarEntrega();
    }

    public void cancelar() {
        estado = getEstadoActual().cancelar().tipo();
    }

    public boolean estaActivo() {
        return getEstadoActual().esActivo();
    }

    // ------------------------------------------------------------ getters

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Establecimiento getEstablecimiento() {
        return establecimiento;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public List<ItemPedido> getItems() {
        return Collections.unmodifiableList(items);
    }

    public EstadoPedidoTipo getEstado() {
        return estado;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public Ubicacion getDestino() {
        return destino;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getCostoEnvio() {
        return costoEnvio;
    }

    public double getDescuento() {
        return descuento;
    }

    public double getTotal() {
        return total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }

    public String getDescuentosAplicados() {
        return descuentosAplicados;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public LocalDateTime getEntregadoEn() {
        return entregadoEn;
    }
}
