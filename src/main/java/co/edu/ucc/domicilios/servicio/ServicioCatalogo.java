package co.edu.ucc.domicilios.servicio;

import co.edu.ucc.domicilios.dominio.establecimiento.Establecimiento;
import co.edu.ucc.domicilios.dto.EstablecimientoDTO;
import co.edu.ucc.domicilios.dto.ProductoDisponibleDTO;
import co.edu.ucc.domicilios.excepcion.RecursoNoEncontradoException;
import co.edu.ucc.domicilios.repositorio.EstablecimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ServicioCatalogo {

    private final EstablecimientoRepository establecimientos;

    public ServicioCatalogo(EstablecimientoRepository establecimientos) {
        this.establecimientos = establecimientos;
    }

    public List<EstablecimientoDTO> listarEstablecimientos() {
        return establecimientos.findAll().stream().map(EstablecimientoDTO::desde).toList();
    }

    /** Productos del establecimiento con sus existencias, para que el cliente sepa qué puede pedir. */
    public List<ProductoDisponibleDTO> catalogo(Long establecimientoId) {
        return buscar(establecimientoId).getInventario().getItems().stream()
                .map(ProductoDisponibleDTO::desde)
                .toList();
    }

    Establecimiento buscar(Long id) {
        return establecimientos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Establecimiento", id));
    }
}
