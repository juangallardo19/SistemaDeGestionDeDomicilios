package co.edu.ucc.domicilios.repositorio;

import co.edu.ucc.domicilios.dominio.usuario.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCorreo(String correo);
}
