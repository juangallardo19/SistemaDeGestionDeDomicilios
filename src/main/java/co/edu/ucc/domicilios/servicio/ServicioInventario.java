package co.edu.ucc.domicilios.servicio;

import co.edu.ucc.domicilios.dominio.establecimiento.Establecimiento;
import co.edu.ucc.domicilios.dominio.establecimiento.Producto;
import co.edu.ucc.domicilios.dto.ItemInventarioDTO;
import co.edu.ucc.domicilios.excepcion.RecursoNoEncontradoException;
import co.edu.ucc.domicilios.excepcion.ReglaNegocioException;
import co.edu.ucc.domicilios.repositorio.EstablecimientoRepository;
import co.edu.ucc.domicilios.repositorio.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Casos de uso del establecimiento sobre sus existencias. */
@Service
@Transactional
public class ServicioInventario {

    private final EstablecimientoRepository establecimientos;
    private final ProductoRepository productos;

    public ServicioInventario(EstablecimientoRepository establecimientos, ProductoRepository productos) {
        this.establecimientos = establecimientos;
        this.productos = productos;
    }

    @Transactional(readOnly = true)
    public List<ItemInventarioDTO> inventario(Long establecimientoId) {
        return buscar(establecimientoId).getInventario().getItems().stream()
                .map(ItemInventarioDTO::desde).toList();
    }

    public ItemInventarioDTO ajustar(Long establecimientoId, Long productoId, int nuevaCantidad) {
        Establecimiento establecimiento = buscar(establecimientoId);
        Producto producto = productos.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", productoId));
        if (!establecimiento.ofrece(producto)) {
            throw new ReglaNegocioException(producto.getNombre() + " no pertenece a " + establecimiento.getNombre());
        }
        establecimiento.getInventario().ajustar(producto, nuevaCantidad);
        return ItemInventarioDTO.desde(establecimiento.getInventario().itemDe(producto));
    }

    private Establecimiento buscar(Long id) {
        return establecimientos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Establecimiento", id));
    }
}
