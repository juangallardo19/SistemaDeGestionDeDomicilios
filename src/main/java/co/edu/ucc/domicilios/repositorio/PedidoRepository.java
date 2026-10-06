package co.edu.ucc.domicilios.repositorio;

import co.edu.ucc.domicilios.dominio.pedido.Pedido;
import co.edu.ucc.domicilios.patrones.estado.EstadoPedidoTipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteIdOrderByCreadoEnDesc(Long clienteId);

    List<Pedido> findByEstablecimientoIdOrderByCreadoEnDesc(Long establecimientoId);

    long countByClienteIdAndEstadoNot(Long clienteId, EstadoPedidoTipo estado);
}
