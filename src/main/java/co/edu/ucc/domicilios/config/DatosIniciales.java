package co.edu.ucc.domicilios.config;

import co.edu.ucc.domicilios.dominio.establecimiento.Categoria;
import co.edu.ucc.domicilios.dominio.establecimiento.Establecimiento;
import co.edu.ucc.domicilios.dominio.establecimiento.Producto;
import co.edu.ucc.domicilios.dominio.pedido.Pedido;
import co.edu.ucc.domicilios.dominio.ubicacion.Ruta;
import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;
import co.edu.ucc.domicilios.dominio.usuario.Administrador;
import co.edu.ucc.domicilios.dominio.usuario.Cliente;
import co.edu.ucc.domicilios.dominio.usuario.Repartidor;
import co.edu.ucc.domicilios.patrones.descuento.SinDescuento;
import co.edu.ucc.domicilios.patrones.pago.PagoEfectivo;
import co.edu.ucc.domicilios.repositorio.ClienteRepository;
import co.edu.ucc.domicilios.repositorio.EstablecimientoRepository;
import co.edu.ucc.domicilios.repositorio.PedidoRepository;
import co.edu.ucc.domicilios.repositorio.RepartidorRepository;
import co.edu.ucc.domicilios.servicio.CalculadorCostoEnvio;
import jakarta.persistence.EntityManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carga datos de ejemplo en Bogotá: 3 establecimientos con su inventario,
 * 2 clientes, 4 repartidores, 1 administrador y un historial de pedidos entregados.
 */
@Component
@Order(1)
public class DatosIniciales implements CommandLineRunner {

    private final EstablecimientoRepository establecimientos;
    private final ClienteRepository clientes;
    private final RepartidorRepository repartidores;
    private final PedidoRepository pedidos;
    private final CalculadorCostoEnvio calculadorCostoEnvio;
    private final EntityManager entityManager;

    public DatosIniciales(EstablecimientoRepository establecimientos, ClienteRepository clientes,
                          RepartidorRepository repartidores, PedidoRepository pedidos,
                          CalculadorCostoEnvio calculadorCostoEnvio, EntityManager entityManager) {
        this.establecimientos = establecimientos;
        this.clientes = clientes;
        this.repartidores = repartidores;
        this.pedidos = pedidos;
        this.calculadorCostoEnvio = calculadorCostoEnvio;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (establecimientos.count() > 0) {
            return;
        }

        Establecimiento burger = new Establecimiento("La Esquina Burger", "Calle 85 # 11-20",
                new Ubicacion(4.6680, -74.0530));
        Producto clasica = burger.agregarProducto("Hamburguesa clásica", 18000, Categoria.COMIDA, 25, 5);
        burger.agregarProducto("Hamburguesa doble queso", 24000, Categoria.COMIDA, 12, 5);
        Producto papas = burger.agregarProducto("Papas a la francesa", 7000, Categoria.COMIDA, 30, 8);
        burger.agregarProducto("Limonada natural", 6000, Categoria.BEBIDA, 20, 5);
        burger.agregarProducto("Malteada de vainilla", 11000, Categoria.BEBIDA, 4, 5);
        burger.agregarProducto("Brownie con helado", 9000, Categoria.POSTRE, 0, 3);

        Establecimiento pizzeria = new Establecimiento("Pizzería Napoli", "Carrera 15 # 93-60",
                new Ubicacion(4.6790, -74.0440));
        Producto margarita = pizzeria.agregarProducto("Pizza margarita", 32000, Categoria.COMIDA, 10, 3);
        pizzeria.agregarProducto("Pizza pepperoni", 36000, Categoria.COMIDA, 8, 3);
        pizzeria.agregarProducto("Lasaña boloñesa", 28000, Categoria.COMIDA, 3, 4);
        Producto gaseosa = pizzeria.agregarProducto("Gaseosa 1.5 L", 7500, Categoria.BEBIDA, 15, 4);
        pizzeria.agregarProducto("Tiramisú", 12000, Categoria.POSTRE, 6, 2);

        Establecimiento cafe = new Establecimiento("Dulce Aroma Café", "Calle 79 # 8-15",
                new Ubicacion(4.6620, -74.0580));
        cafe.agregarProducto("Café americano", 5000, Categoria.BEBIDA, 40, 10);
        cafe.agregarProducto("Capuchino", 7500, Categoria.BEBIDA, 30, 10);
        cafe.agregarProducto("Croissant de almendras", 8000, Categoria.COMIDA, 2, 5);
        cafe.agregarProducto("Cheesecake de frutos rojos", 10500, Categoria.POSTRE, 9, 3);

        establecimientos.save(burger);
        establecimientos.save(pizzeria);
        establecimientos.save(cafe);

        Cliente ana = clientes.save(new Cliente("Ana Gómez", "ana@correo.com", "3001112233",
                "Calle 100 # 19-61, Apto 502", new Ubicacion(4.6865, -74.0510)));
        clientes.save(new Cliente("Carlos Pérez", "carlos@correo.com", "3104445566",
                "Carrera 7 # 72-41", new Ubicacion(4.6555, -74.0560)));

        Repartidor pedro = repartidores.save(new Repartidor("Pedro Ramírez", "pedro@domicilios.com", "3201000001",
                "Moto", 28, new Ubicacion(4.6700, -74.0500)));
        repartidores.save(new Repartidor("Laura Torres", "laura@domicilios.com", "3201000002",
                "Bicicleta", 16, new Ubicacion(4.6610, -74.0620)));
        Repartidor andres = repartidores.save(new Repartidor("Andrés Castillo", "andres@domicilios.com", "3201000003",
                "Moto", 26, new Ubicacion(4.6810, -74.0420)));
        Repartidor sofia = repartidores.save(new Repartidor("Sofía Moreno", "sofia@domicilios.com", "3201000004",
                "Moto", 25, new Ubicacion(4.6590, -74.0470)));
        sofia.terminarTurno();

        entityManager.persist(new Administrador("Marta Rojas", "admin@domicilios.com", "3159998877", "Operaciones"));

        // Historial: Ana ya compró dos veces (por eso el código BIENVENIDA no le aplica).
        registrarPedidoEntregado(ana, burger, pedro, clasica, 1, papas, 1);
        registrarPedidoEntregado(ana, pizzeria, andres, margarita, 1, gaseosa, 1);
    }

    private void registrarPedidoEntregado(Cliente cliente, Establecimiento establecimiento, Repartidor repartidor,
                                          Producto producto1, int cantidad1, Producto producto2, int cantidad2) {
        Ubicacion posicionInicial = repartidor.getUbicacionActual();
        Pedido pedido = new Pedido(cliente, establecimiento);
        pedido.agregarItem(producto1, cantidad1);
        pedido.agregarItem(producto2, cantidad2);
        pedido.liquidar(calculadorCostoEnvio.calcular(establecimiento.getUbicacion(), cliente.getUbicacion()),
                new SinDescuento());
        pedido.pagar(new PagoEfectivo());
        pedido.iniciarPreparacion();
        pedido.despachar(repartidor, Ruta.trazar(establecimiento.getUbicacion(), cliente.getUbicacion()));
        pedido.marcarEntregado();
        repartidor.actualizarUbicacion(posicionInicial);
        pedidos.save(pedido);
    }
}
