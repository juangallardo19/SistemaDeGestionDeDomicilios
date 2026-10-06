package co.edu.ucc.domicilios.web;

import co.edu.ucc.domicilios.dto.PedidoDTO;
import co.edu.ucc.domicilios.dto.RepartidorDTO;
import co.edu.ucc.domicilios.dto.SeguimientoDTO;
import co.edu.ucc.domicilios.dto.UbicacionDTO;
import co.edu.ucc.domicilios.servicio.ServicioDespacho;
import co.edu.ucc.domicilios.servicio.ServicioRepartidor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Endpoints del repartidor. */
@RestController
@RequestMapping("/api")
public class RepartidorController {

    private final ServicioRepartidor servicioRepartidor;
    private final ServicioDespacho servicioDespacho;

    public RepartidorController(ServicioRepartidor servicioRepartidor, ServicioDespacho servicioDespacho) {
        this.servicioRepartidor = servicioRepartidor;
        this.servicioDespacho = servicioDespacho;
    }

    @GetMapping("/repartidores")
    public List<RepartidorDTO> listar() {
        return servicioRepartidor.listar();
    }

    @GetMapping("/repartidores/{id}/pedidos")
    public List<PedidoDTO> pedidosActivos(@PathVariable Long id) {
        return servicioRepartidor.pedidosActivos(id);
    }

    @PutMapping("/repartidores/{id}/ubicacion")
    public SeguimientoDTO actualizarUbicacion(@PathVariable Long id, @RequestBody UbicacionDTO ubicacion) {
        return servicioRepartidor.actualizarUbicacion(id, ubicacion.aUbicacion());
    }

    /** Simula que el repartidor avanza un tramo de su ruta (~150 m). */
    @PostMapping("/repartidores/{id}/avanzar")
    public SeguimientoDTO avanzar(@PathVariable Long id) {
        return servicioRepartidor.avanzar(id);
    }

    @PutMapping("/repartidores/{id}/turno")
    public RepartidorDTO cambiarTurno(@PathVariable Long id, @RequestParam boolean enTurno) {
        return servicioRepartidor.cambiarTurno(id, enTurno);
    }

    @PostMapping("/pedidos/{id}/entregar")
    public PedidoDTO entregar(@PathVariable Long id) {
        return servicioDespacho.entregar(id);
    }
}
