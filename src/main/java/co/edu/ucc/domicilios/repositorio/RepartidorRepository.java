package co.edu.ucc.domicilios.repositorio;

import co.edu.ucc.domicilios.dominio.usuario.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {

    List<Repartidor> findByEnTurnoTrue();

    Optional<Repartidor> findByCorreo(String correo);
}
