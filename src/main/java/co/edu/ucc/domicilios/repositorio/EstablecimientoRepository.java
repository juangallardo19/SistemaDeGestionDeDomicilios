package co.edu.ucc.domicilios.repositorio;

import co.edu.ucc.domicilios.dominio.establecimiento.Establecimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstablecimientoRepository extends JpaRepository<Establecimiento, Long> {

    Optional<Establecimiento> findByNombre(String nombre);
}
