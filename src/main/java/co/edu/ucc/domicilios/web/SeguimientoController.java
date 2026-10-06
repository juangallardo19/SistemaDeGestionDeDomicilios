package co.edu.ucc.domicilios.web;

import co.edu.ucc.domicilios.dto.SeguimientoDTO;
import co.edu.ucc.domicilios.patrones.evento.NotificadorCliente;
import co.edu.ucc.domicilios.servicio.ServicioSeguimiento;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Seguimiento del pedido para el cliente. */
@RestController
@RequestMapping("/api/pedidos/{id}")
public class SeguimientoController {

    private final ServicioSeguimiento servicioSeguimiento;
    private final NotificadorCliente notificadorCliente;

    public SeguimientoController(ServicioSeguimiento servicioSeguimiento, NotificadorCliente notificadorCliente) {
        this.servicioSeguimiento = servicioSeguimiento;
        this.notificadorCliente = notificadorCliente;
    }

    @GetMapping("/seguimiento")
    public SeguimientoDTO seguimiento(@PathVariable Long id) {
        return servicioSeguimiento.seguimiento(id);
    }

    @GetMapping("/notificaciones")
    public List<String> notificaciones(@PathVariable Long id) {
        return notificadorCliente.notificacionesDe(id);
    }
}
