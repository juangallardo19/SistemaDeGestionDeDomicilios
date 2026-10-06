package co.edu.ucc.domicilios.web;

import co.edu.ucc.domicilios.dto.CambioEstrategiasRequest;
import co.edu.ucc.domicilios.dto.CargaRepartidorDTO;
import co.edu.ucc.domicilios.dto.DisponibilidadEstablecimientoDTO;
import co.edu.ucc.domicilios.dto.EstrategiasDTO;
import co.edu.ucc.domicilios.patrones.evento.EventoPedido;
import co.edu.ucc.domicilios.servicio.ServicioAdministracion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Panel del administrador. */
@RestController
@RequestMapping("/api/admin")
public class AdministracionController {

    private final ServicioAdministracion servicioAdministracion;

    public AdministracionController(ServicioAdministracion servicioAdministracion) {
        this.servicioAdministracion = servicioAdministracion;
    }

    @GetMapping("/repartidores/productos")
    public List<CargaRepartidorDTO> productosPorRepartidor() {
        return servicioAdministracion.productosPorRepartidor();
    }

    @GetMapping("/establecimientos/disponibilidad")
    public List<DisponibilidadEstablecimientoDTO> disponibilidad() {
        return servicioAdministracion.disponibilidadPorEstablecimiento();
    }

    @GetMapping("/bitacora")
    public List<EventoPedido> bitacora() {
        return servicioAdministracion.bitacora();
    }

    @GetMapping("/estrategias")
    public EstrategiasDTO estrategias() {
        return servicioAdministracion.estrategias();
    }

    @PutMapping("/estrategias")
    public EstrategiasDTO cambiarEstrategias(@RequestBody CambioEstrategiasRequest cambio) {
        return servicioAdministracion.cambiarEstrategias(cambio.asignacion(), cambio.tiempo());
    }
}
