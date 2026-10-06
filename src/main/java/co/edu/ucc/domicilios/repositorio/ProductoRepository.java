package co.edu.ucc.domicilios.repositorio;

import co.edu.ucc.domicilios.dominio.establecimiento.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByNombreAndEstablecimientoId(String nombre, Long establecimientoId);
}
