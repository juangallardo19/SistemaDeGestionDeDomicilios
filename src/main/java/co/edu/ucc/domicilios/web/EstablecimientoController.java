package co.edu.ucc.domicilios.web;

import co.edu.ucc.domicilios.dto.AjusteStockRequest;
import co.edu.ucc.domicilios.dto.ItemInventarioDTO;
import co.edu.ucc.domicilios.dto.PedidoDTO;
import co.edu.ucc.domicilios.servicio.ServicioDespacho;
import co.edu.ucc.domicilios.servicio.ServicioInventario;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Endpoints del establecimiento: pedidos entrantes, preparación, despacho e inventario. */
@RestController
@RequestMapping("/api")
public class EstablecimientoController {

    private final ServicioDespacho servicioDespacho;
    private final ServicioInventario servicioInventario;

    public EstablecimientoController(ServicioDespacho servicioDespacho, ServicioInventario servicioInventario) {
        this.servicioDespacho = servicioDespacho;
        this.servicioInventario = servicioInventario;
    }

    @GetMapping("/establecimientos/{id}/pedidos")
    public List<PedidoDTO> pedidos(@PathVariable Long id) {
        return servicioDespacho.pedidosDeEstablecimiento(id);
    }

    @PostMapping("/pedidos/{id}/preparar")
    public PedidoDTO preparar(@PathVariable Long id) {
        return servicioDespacho.preparar(id);
    }

    @PostMapping("/pedidos/{id}/despachar")
    public PedidoDTO despachar(@PathVariable Long id) {
        return servicioDespacho.despachar(id);
    }

    @GetMapping("/establecimientos/{id}/inventario")
    public List<ItemInventarioDTO> inventario(@PathVariable Long id) {
        return servicioInventario.inventario(id);
    }

    @PutMapping("/establecimientos/{id}/inventario/{productoId}")
    public ItemInventarioDTO ajustarStock(@PathVariable Long id, @PathVariable Long productoId,
                                          @Valid @RequestBody AjusteStockRequest ajuste) {
        return servicioInventario.ajustar(id, productoId, ajuste.cantidad());
    }
}
