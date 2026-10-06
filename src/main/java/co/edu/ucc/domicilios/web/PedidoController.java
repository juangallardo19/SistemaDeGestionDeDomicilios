package co.edu.ucc.domicilios.web;

import co.edu.ucc.domicilios.dto.CotizacionDTO;
import co.edu.ucc.domicilios.dto.PedidoDTO;
import co.edu.ucc.domicilios.dto.SolicitudPedido;
import co.edu.ucc.domicilios.servicio.ServicioPedidos;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Endpoints del cliente. */
@RestController
@RequestMapping("/api")
public class PedidoController {

    private final ServicioPedidos servicioPedidos;

    public PedidoController(ServicioPedidos servicioPedidos) {
        this.servicioPedidos = servicioPedidos;
    }

    @PostMapping("/pedidos/cotizacion")
    public CotizacionDTO cotizar(@Valid @RequestBody SolicitudPedido solicitud) {
        return servicioPedidos.cotizar(solicitud);
    }

    @PostMapping("/pedidos")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoDTO crear(@Valid @RequestBody SolicitudPedido solicitud) {
        return servicioPedidos.crear(solicitud);
    }

    @GetMapping("/pedidos/{id}")
    public PedidoDTO consultar(@PathVariable Long id) {
        return servicioPedidos.consultar(id);
    }

    @GetMapping("/clientes/{clienteId}/pedidos")
    public List<PedidoDTO> pedidosDeCliente(@PathVariable Long clienteId) {
        return servicioPedidos.pedidosDeCliente(clienteId);
    }

    @PostMapping("/pedidos/{id}/cancelar")
    public PedidoDTO cancelar(@PathVariable Long id) {
        return servicioPedidos.cancelar(id);
    }
}
