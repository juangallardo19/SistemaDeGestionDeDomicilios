package co.edu.ucc.domicilios.web;

import co.edu.ucc.domicilios.dto.EstablecimientoDTO;
import co.edu.ucc.domicilios.dto.ProductoDisponibleDTO;
import co.edu.ucc.domicilios.servicio.ServicioCatalogo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/establecimientos")
public class CatalogoController {

    private final ServicioCatalogo servicioCatalogo;

    public CatalogoController(ServicioCatalogo servicioCatalogo) {
        this.servicioCatalogo = servicioCatalogo;
    }

    @GetMapping
    public List<EstablecimientoDTO> listar() {
        return servicioCatalogo.listarEstablecimientos();
    }

    @GetMapping("/{id}/productos")
    public List<ProductoDisponibleDTO> catalogo(@PathVariable Long id) {
        return servicioCatalogo.catalogo(id);
    }
}
