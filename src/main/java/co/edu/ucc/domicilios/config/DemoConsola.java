package co.edu.ucc.domicilios.config;

import co.edu.ucc.domicilios.dto.CargaRepartidorDTO;
import co.edu.ucc.domicilios.dto.CotizacionDTO;
import co.edu.ucc.domicilios.dto.DisponibilidadEstablecimientoDTO;
import co.edu.ucc.domicilios.dto.EstablecimientoDTO;
import co.edu.ucc.domicilios.dto.ItemInventarioDTO;
import co.edu.ucc.domicilios.dto.ItemPedidoDTO;
import co.edu.ucc.domicilios.dto.ItemSolicitud;
import co.edu.ucc.domicilios.dto.PedidoDTO;
import co.edu.ucc.domicilios.dto.ProductoDisponibleDTO;
import co.edu.ucc.domicilios.dto.RepartidorDTO;
import co.edu.ucc.domicilios.dto.SeguimientoDTO;
import co.edu.ucc.domicilios.dto.SolicitudPedido;
import co.edu.ucc.domicilios.excepcion.ReglaNegocioException;
import co.edu.ucc.domicilios.patrones.asignacion.AsignacionMasCercano;
import co.edu.ucc.domicilios.patrones.asignacion.AsignacionMenosOcupado;
import co.edu.ucc.domicilios.patrones.tiempo.CalculadorPorVelocidadPromedio;
import co.edu.ucc.domicilios.repositorio.ClienteRepository;
import co.edu.ucc.domicilios.servicio.ServicioAdministracion;
import co.edu.ucc.domicilios.servicio.ServicioCatalogo;
import co.edu.ucc.domicilios.servicio.ServicioDespacho;
import co.edu.ucc.domicilios.servicio.ServicioPedidos;
import co.edu.ucc.domicilios.servicio.ServicioRepartidor;
import co.edu.ucc.domicilios.servicio.ServicioSeguimiento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Demo por consola: recorre el flujo completo de la propuesta usando los mismos
 * servicios que expone la API REST. Se desactiva con domicilios.demo.consola=false.
 */
@Component
@Order(2)
@ConditionalOnProperty(name = "domicilios.demo.consola", havingValue = "true", matchIfMissing = true)
public class DemoConsola implements CommandLineRunner {

    private static final Locale COLOMBIA = Locale.forLanguageTag("es-CO");

    private final ServicioCatalogo catalogo;
    private final ServicioPedidos servicioPedidos;
    private final ServicioDespacho despacho;
    private final ServicioRepartidor servicioRepartidor;
    private final ServicioSeguimiento seguimiento;
    private final ServicioAdministracion administracion;
    private final ClienteRepository clientes;
    private final int puerto;

    private int paso;

    public DemoConsola(ServicioCatalogo catalogo, ServicioPedidos servicioPedidos, ServicioDespacho despacho,
                       ServicioRepartidor servicioRepartidor, ServicioSeguimiento seguimiento,
                       ServicioAdministracion administracion, ClienteRepository clientes,
                       @Value("${server.port:8080}") int puerto) {
        this.catalogo = catalogo;
        this.servicioPedidos = servicioPedidos;
        this.despacho = despacho;
        this.servicioRepartidor = servicioRepartidor;
        this.seguimiento = seguimiento;
        this.administracion = administracion;
        this.clientes = clientes;
        this.puerto = puerto;
    }

    @Override
    public void run(String... args) {
        System.out.println();
        System.out.println("==========================================================================");
        System.out.println("              PLATAFORMA DE DOMICILIOS - DEMO POR CONSOLA");
        System.out.println("==========================================================================");

        Long carlos = clientes.findByCorreo("carlos@correo.com").orElseThrow().getId();
        Long ana = clientes.findByCorreo("ana@correo.com").orElseThrow().getId();
        EstablecimientoDTO burger = establecimiento("La Esquina Burger");
        EstablecimientoDTO pizzeria = establecimiento("Pizzería Napoli");
        Map<String, Long> menuBurger = productos(burger.id());
        Map<String, Long> menuPizzeria = productos(pizzeria.id());

        // 1. Catálogo -----------------------------------------------------------------
        titulo("El cliente explora los establecimientos y su disponibilidad");
        catalogo.listarEstablecimientos().forEach(e ->
                System.out.printf("   - %-20s %-22s %d productos%n", e.nombre(), e.direccion(), e.productos()));
        System.out.println();
        System.out.println("   Menú de " + burger.nombre() + ":");
        for (ProductoDisponibleDTO p : catalogo.catalogo(burger.id())) {
            System.out.printf("     %-26s %-7s %10s   disponibles: %2d  [%s]%n",
                    p.nombre(), p.categoria(), dinero(p.precio()), p.disponibles(), p.nivel());
        }

        // 2. Cotización con descuentos apilados (Decorator) --------------------------------
        titulo("Carlos arma su pedido y aplica descuentos apilados (patrón Decorator)");
        SolicitudPedido pedidoCarlos = new SolicitudPedido(carlos, burger.id(), List.of(
                new ItemSolicitud(menuBurger.get("Hamburguesa doble queso"), 2),
                new ItemSolicitud(menuBurger.get("Papas a la francesa"), 2),
                new ItemSolicitud(menuBurger.get("Limonada natural"), 2)),
                "TARJETA", List.of("BIENVENIDA", "ENVIOGRATIS", "DOMI10"));
        CotizacionDTO cotizacion = servicioPedidos.cotizar(pedidoCarlos);
        System.out.println("   Códigos: BIENVENIDA + ENVIOGRATIS + DOMI10");
        System.out.printf("   Subtotal productos ........ %12s%n", dinero(cotizacion.subtotal()));
        System.out.printf("   Domicilio (%.2f km) ........ %12s%n", cotizacion.distanciaKm(), dinero(cotizacion.costoEnvio()));
        System.out.printf("   Descuentos ................ %12s%n", "-" + dinero(cotizacion.descuento()));
        System.out.printf("   TOTAL ..................... %12s%n", dinero(cotizacion.total()));
        System.out.println("   Cadena aplicada: " + cotizacion.descuentosAplicados());

        // 3. Reglas de negocio ----------------------------------------------------------
        titulo("Validaciones: el sistema protege las reglas del negocio");
        intentar("Carlos pide 10 malteadas (solo hay 4)", () -> servicioPedidos.cotizar(new SolicitudPedido(
                carlos, burger.id(), List.of(new ItemSolicitud(menuBurger.get("Malteada de vainilla"), 10)),
                "EFECTIVO", List.of())));
        intentar("Carlos pide un brownie (agotado)", () -> servicioPedidos.cotizar(new SolicitudPedido(
                carlos, burger.id(), List.of(new ItemSolicitud(menuBurger.get("Brownie con helado"), 1)),
                "EFECTIVO", List.of())));
        intentar("Ana usa BIENVENIDA (ya tiene compras)", () -> servicioPedidos.cotizar(new SolicitudPedido(
                ana, pizzeria.id(), List.of(new ItemSolicitud(menuPizzeria.get("Tiramisú"), 1)),
                "EFECTIVO", List.of("BIENVENIDA"))));
        intentar("Pago con un método inexistente", () -> servicioPedidos.crear(new SolicitudPedido(
                carlos, burger.id(), List.of(new ItemSolicitud(menuBurger.get("Limonada natural"), 1)),
                "BITCOIN", List.of())));

        // 4. Pedido y pago (Strategy de pago + Observer) ---------------------------------
        titulo("Carlos confirma y paga con tarjeta (polimorfismo de MetodoPago + Observer)");
        PedidoDTO pedido = servicioPedidos.crear(pedidoCarlos);
        imprimirPedido(pedido);

        // 5. State ----------------------------------------------------------------------
        titulo("Ciclo de vida del pedido (patrón State)");
        intentar("Entregar un pedido que apenas fue RECIBIDO", () -> despacho.entregar(pedido.id()));
        intentar("Despachar sin haberlo preparado", () -> despacho.despachar(pedido.id()));

        Map<String, Integer> antes = existencias(burger.id());
        PedidoDTO preparando = despacho.preparar(pedido.id());
        System.out.println("   Estado: " + preparando.estado() + " -> se descontó el inventario:");
        Map<String, Integer> despues = existencias(burger.id());
        for (ItemPedidoDTO item : preparando.items()) {
            System.out.printf("     %-26s %3d -> %3d%n", item.producto(), antes.get(item.producto()),
                    despues.get(item.producto()));
        }
        intentar("Cancelar un pedido que ya se está preparando", () -> servicioPedidos.cancelar(pedido.id()));

        // 6. Asignación (Strategy) -------------------------------------------------------
        titulo("Asignación del repartidor (patrón Strategy: '" + AsignacionMasCercano.NOMBRE + "')");
        imprimirRepartidores(burger);
        PedidoDTO enCamino = despacho.despachar(pedido.id());
        System.out.println("   => Asignado a: " + enCamino.repartidor() + " | estado: " + enCamino.estado());

        // 7. Panel del administrador ------------------------------------------------------
        titulo("Panel del administrador: productos que lleva cada domiciliario");
        imprimirCargaRepartidores();

        // 8. Seguimiento ------------------------------------------------------------------
        titulo("Seguimiento en tiempo real: el repartidor avanza por la ruta (Observer + Strategy ETA)");
        Long repartidorId = repartidorPorNombre(enCamino.repartidor());
        SeguimientoDTO estado = seguimiento.seguimiento(pedido.id());
        System.out.printf("   Ruta de %d puntos, %.2f km. Estimación inicial: %d min (%s)%n",
                estado.restante().size(), estado.distanciaRestanteKm(), estado.minutosEstimados(),
                estado.calculadorTiempo());
        System.out.println("   Tiempo estimado según cada estrategia (Strategy):");
        seguimiento.estimacionesPorEstrategia(pedido.id()).forEach((nombre, minutos) ->
                System.out.printf("        %-20s %2d min%n", nombre, minutos));
        while (estado.progreso() < 100) {
            estado = servicioRepartidor.avanzar(repartidorId);
        }
        PedidoDTO entregado = despacho.entregar(pedido.id());
        System.out.println("   Estado final: " + entregado.estado() + " a las " + entregado.entregadoEn().toLocalTime().withNano(0));

        // 9. Segundo pedido con otra estrategia ------------------------------------------
        titulo("Ana pide en Pizzería Napoli con efectivo; el admin cambia la estrategia a '"
                + AsignacionMenosOcupado.NOMBRE + "'");
        administracion.cambiarEstrategias(AsignacionMenosOcupado.NOMBRE, CalculadorPorVelocidadPromedio.NOMBRE);
        PedidoDTO pedidoAna = servicioPedidos.crear(new SolicitudPedido(ana, pizzeria.id(), List.of(
                new ItemSolicitud(menuPizzeria.get("Pizza pepperoni"), 1),
                new ItemSolicitud(menuPizzeria.get("Lasaña boloñesa"), 2),
                new ItemSolicitud(menuPizzeria.get("Gaseosa 1.5 L"), 1)),
                "EFECTIVO", List.of("DOMI20")));
        despacho.preparar(pedidoAna.id());
        imprimirRepartidores(pizzeria);
        PedidoDTO pedidoAnaEnCamino = despacho.despachar(pedidoAna.id());
        System.out.println("   => Asignado a: " + pedidoAnaEnCamino.repartidor()
                + " (el que menos entregas tiene, aunque no sea el más cercano)");

        // 10. Estado final para el administrador ------------------------------------------
        titulo("Panel del administrador: estado final");
        imprimirCargaRepartidores();
        System.out.println();
        System.out.println("   Disponibilidad por establecimiento:");
        for (DisponibilidadEstablecimientoDTO d : administracion.disponibilidadPorEstablecimiento()) {
            System.out.println("   * " + d.establecimiento());
            if (d.alertas().isEmpty()) {
                System.out.println("       sin alertas");
            }
            d.alertas().forEach(alerta -> System.out.println("       ALERTA: " + alerta));
        }

        System.out.println();
        System.out.println("==========================================================================");
        System.out.println(" Demo terminada. La API REST sigue activa en http://localhost:" + puerto + "/api");
        System.out.println(" Pedido #" + pedidoAna.id() + " quedó EN_CAMINO para probar el seguimiento con Postman:");
        System.out.println("   POST http://localhost:" + puerto + "/api/repartidores/" + repartidorPorNombre(pedidoAnaEnCamino.repartidor()) + "/avanzar");
        System.out.println("   GET  http://localhost:" + puerto + "/api/pedidos/" + pedidoAna.id() + "/seguimiento");
        System.out.println(" Consola de base de datos H2: http://localhost:" + puerto + "/h2-console");
        System.out.println("==========================================================================");
        System.out.println();
    }

    // ------------------------------------------------------------------ utilidades de impresión

    private void titulo(String texto) {
        System.out.println();
        System.out.println("--------------------------------------------------------------------------");
        System.out.println(" PASO " + (++paso) + ". " + texto);
        System.out.println("--------------------------------------------------------------------------");
    }

    private void intentar(String descripcion, Runnable accion) {
        try {
            accion.run();
            System.out.println("   [OK]     " + descripcion);
        } catch (ReglaNegocioException e) {
            System.out.println("   [RECHAZADO] " + descripcion);
            System.out.println("               -> " + e.getMessage());
        }
    }

    private void imprimirPedido(PedidoDTO p) {
        System.out.println("   Pedido #" + p.id() + " de " + p.cliente() + " en " + p.establecimiento());
        p.items().forEach(i -> System.out.printf("     %2d x %-26s %10s%n", i.cantidad(), i.producto(), dinero(i.subtotal())));
        System.out.printf("   Total pagado: %s con %s (ref. %s)%n", dinero(p.total()), p.metodoPago(), p.referenciaPago());
        System.out.println("   Estado: " + p.estado() + " - " + p.descripcionEstado());
    }

    private void imprimirRepartidores(EstablecimientoDTO origen) {
        System.out.println("   Repartidores (distancia al establecimiento / entregas realizadas):");
        for (RepartidorDTO r : servicioRepartidor.listar()) {
            double distancia = r.ubicacion().aUbicacion().distanciaKmA(origen.ubicacion().aUbicacion());
            String estado = !r.enTurno() ? "fuera de turno" : r.disponible() ? "disponible" : "ocupado";
            System.out.printf("     %-17s %-10s %5.2f km   %d entregas   %s%n",
                    r.nombre(), r.vehiculo(), distancia, r.entregasRealizadas(), estado);
        }
    }

    private void imprimirCargaRepartidores() {
        for (CargaRepartidorDTO carga : administracion.productosPorRepartidor()) {
            if (carga.pedidosActivos().isEmpty()) {
                System.out.printf("   %-17s sin pedidos activos%s%n", carga.repartidor(), carga.enTurno() ? "" : " (fuera de turno)");
                continue;
            }
            System.out.printf("   %-17s lleva %d unidades:%n", carga.repartidor(), carga.totalUnidades());
            carga.pedidosActivos().forEach(p -> {
                System.out.println("       Pedido #" + p.pedidoId() + " para " + p.cliente() + " (" + p.direccion() + ")");
                p.productos().forEach(prod -> System.out.printf("         %2d x %s%n", prod.cantidad(), prod.producto()));
            });
        }
    }

    private EstablecimientoDTO establecimiento(String nombre) {
        return catalogo.listarEstablecimientos().stream()
                .filter(e -> e.nombre().equals(nombre)).findFirst().orElseThrow();
    }

    private Map<String, Long> productos(Long establecimientoId) {
        Map<String, Long> ids = new java.util.LinkedHashMap<>();
        catalogo.catalogo(establecimientoId).forEach(p -> ids.put(p.nombre(), p.id()));
        return ids;
    }

    private Map<String, Integer> existencias(Long establecimientoId) {
        Map<String, Integer> cantidades = new java.util.HashMap<>();
        for (ItemInventarioDTO item : administracion.disponibilidadPorEstablecimiento().stream()
                .filter(d -> d.establecimientoId().equals(establecimientoId)).findFirst().orElseThrow().inventario()) {
            cantidades.put(item.producto(), item.cantidad());
        }
        return cantidades;
    }

    private Long repartidorPorNombre(String nombre) {
        return servicioRepartidor.listar().stream()
                .filter(r -> r.nombre().equals(nombre)).findFirst().orElseThrow().id();
    }

    private static String dinero(double valor) {
        return String.format(COLOMBIA, "$%,.0f", valor);
    }
}
