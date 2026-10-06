package co.edu.ucc.domicilios.dominio;

import co.edu.ucc.domicilios.dominio.establecimiento.Categoria;
import co.edu.ucc.domicilios.dominio.establecimiento.Establecimiento;
import co.edu.ucc.domicilios.dominio.establecimiento.Producto;
import co.edu.ucc.domicilios.dominio.pedido.Pedido;
import co.edu.ucc.domicilios.dominio.ubicacion.Ruta;
import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;
import co.edu.ucc.domicilios.dominio.usuario.Cliente;
import co.edu.ucc.domicilios.dominio.usuario.Repartidor;
import co.edu.ucc.domicilios.excepcion.ReglaNegocioException;
import co.edu.ucc.domicilios.excepcion.TransicionInvalidaException;
import co.edu.ucc.domicilios.patrones.estado.EstadoPedidoTipo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PedidoEstadoTest {

    private Establecimiento establecimiento;
    private Producto hamburguesa;
    private Pedido pedido;
    private Repartidor repartidor;

    @BeforeEach
    void preparar() {
        establecimiento = new Establecimiento("Burger", "Calle 1", new Ubicacion(4.66, -74.05));
        hamburguesa = establecimiento.agregarProducto("Hamburguesa", 18000, Categoria.COMIDA, 10, 2);
        Cliente cliente = new Cliente("Ana", "ana@x.com", "300", "Calle 100", new Ubicacion(4.68, -74.05));
        pedido = new Pedido(cliente, establecimiento);
        pedido.agregarItem(hamburguesa, 2);
        repartidor = new Repartidor("Pedro", "pedro@x.com", "320", "Moto", 30, new Ubicacion(4.67, -74.05));
    }

    @Test
    void recorreElCicloDeVidaCompleto() {
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedidoTipo.RECIBIDO);

        pedido.iniciarPreparacion();
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedidoTipo.PREPARANDO);

        pedido.despachar(repartidor, Ruta.trazar(establecimiento.getUbicacion(), pedido.getDestino()));
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedidoTipo.EN_CAMINO);
        assertThat(repartidor.getPedidosActivos()).containsExactly(pedido);
        assertThat(repartidor.estaDisponible()).isFalse();

        pedido.marcarEntregado();
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedidoTipo.ENTREGADO);
        assertThat(repartidor.getPedidosActivos()).isEmpty();
        assertThat(repartidor.getEntregasRealizadas()).isEqualTo(1);
    }

    @Test
    void noSePuedeSaltarDeRecibidoAEntregado() {
        assertThatThrownBy(pedido::marcarEntregado)
                .isInstanceOf(TransicionInvalidaException.class)
                .hasMessageContaining("RECIBIDO");
    }

    @Test
    void soloSeCancelaMientrasEstaRecibido() {
        pedido.iniciarPreparacion();
        assertThatThrownBy(pedido::cancelar).isInstanceOf(TransicionInvalidaException.class);
    }

    @Test
    void unRepartidorOcupadoNoRecibeOtroPedido() {
        pedido.iniciarPreparacion();
        pedido.despachar(repartidor, Ruta.trazar(establecimiento.getUbicacion(), pedido.getDestino()));

        Pedido otro = new Pedido(pedido.getCliente(), establecimiento);
        otro.agregarItem(hamburguesa, 1);
        otro.iniciarPreparacion();

        assertThatThrownBy(() -> otro.despachar(repartidor, Ruta.trazar(establecimiento.getUbicacion(), otro.getDestino())))
                .isInstanceOf(ReglaNegocioException.class);
    }
}
