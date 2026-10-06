package co.edu.ucc.domicilios.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Recorre el flujo completo por la API REST, igual que se haría desde Postman. */
@SpringBootTest(properties = "domicilios.demo.consola=false")
@AutoConfigureMockMvc
class FlujoPedidoIntegracionTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    @Test
    void flujoCompletoDeUnPedido() throws Exception {
        JsonNode menu = leer(get("/api/establecimientos/1/productos"));
        long hamburguesa = menu.get(0).get("id").asLong();

        String solicitud = """
                {"clienteId": 2, "establecimientoId": 1, "metodoPago": "BILLETERA",
                 "codigosDescuento": ["BIENVENIDA"],
                 "items": [{"productoId": %d, "cantidad": 2}]}
                """.formatted(hamburguesa);

        JsonNode pedido = json.readTree(mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON).content(solicitud))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado", is("RECIBIDO")))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8));
        long id = pedido.get("id").asLong();

        mvc.perform(post("/api/pedidos/{id}/entregar", id)).andExpect(status().isConflict());
        mvc.perform(post("/api/pedidos/{id}/preparar", id)).andExpect(jsonPath("$.estado", is("PREPARANDO")));
        JsonNode despachado = leer(post("/api/pedidos/{id}/despachar", id));
        long repartidor = buscarRepartidor(despachado.get("repartidor").asText());

        mvc.perform(get("/api/admin/repartidores/productos"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/repartidores/{id}/avanzar", repartidor))
                .andExpect(jsonPath("$.estado", is("EN_CAMINO")));
        mvc.perform(get("/api/pedidos/{id}/seguimiento", id))
                .andExpect(jsonPath("$.repartidor").isNotEmpty())
                .andExpect(jsonPath("$.minutosEstimados").isNumber());
        mvc.perform(post("/api/pedidos/{id}/entregar", id)).andExpect(jsonPath("$.estado", is("ENTREGADO")));
        mvc.perform(get("/api/pedidos/{id}/notificaciones", id)).andExpect(jsonPath("$.length()", is(5)));
    }

    @Test
    void rechazaPedidosSinExistencias() throws Exception {
        JsonNode menu = leer(get("/api/establecimientos/1/productos"));
        long agotado = 0;
        for (JsonNode producto : menu) {
            if (producto.get("disponibles").asInt() == 0) {
                agotado = producto.get("id").asLong();
            }
        }
        String solicitud = """
                {"clienteId": 1, "establecimientoId": 1, "metodoPago": "EFECTIVO",
                 "items": [{"productoId": %d, "cantidad": 1}]}
                """.formatted(agotado);

        mvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON).content(solicitud))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    private long buscarRepartidor(String nombre) throws Exception {
        for (JsonNode r : leer(get("/api/repartidores"))) {
            if (r.get("nombre").asText().equals(nombre)) {
                return r.get("id").asLong();
            }
        }
        throw new IllegalStateException("No existe el repartidor " + nombre);
    }

    private JsonNode leer(org.springframework.test.web.servlet.RequestBuilder peticion) throws Exception {
        return json.readTree(mvc.perform(peticion).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8));
    }
}
