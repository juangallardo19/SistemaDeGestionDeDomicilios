package co.edu.ucc.domicilios.patrones;

import co.edu.ucc.domicilios.excepcion.ReglaNegocioException;
import co.edu.ucc.domicilios.patrones.descuento.Descuento;
import co.edu.ucc.domicilios.patrones.descuento.DescuentoCupon;
import co.edu.ucc.domicilios.patrones.descuento.DescuentoEnvioGratis;
import co.edu.ucc.domicilios.patrones.descuento.DescuentoPrimeraCompra;
import co.edu.ucc.domicilios.patrones.descuento.DetalleCosto;
import co.edu.ucc.domicilios.patrones.descuento.FabricaDescuentos;
import co.edu.ucc.domicilios.patrones.descuento.SinDescuento;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescuentoDecoratorTest {

    private final DetalleCosto costo = new DetalleCosto(50000, 5000);

    @Test
    void sinDescuentoCobraProductosMasDomicilio() {
        assertThat(new SinDescuento().aplicar(costo)).isEqualTo(55000);
    }

    @Test
    void losDecoradoresSeApilan() {
        Descuento descuento = new DescuentoEnvioGratis(
                new DescuentoCupon(new DescuentoPrimeraCompra(new SinDescuento(), 5000), "DOMI10", 10));

        // 55.000 - 5.000 (primera compra) - 5.000 (10% de 50.000) - 5.000 (envío) = 40.000
        assertThat(descuento.aplicar(costo)).isEqualTo(40000);
        assertThat(descuento.descripcion()).contains("Primera compra", "DOMI10", "Envío gratis");
    }

    @Test
    void elTotalNuncaEsNegativo() {
        Descuento descuento = new DescuentoPrimeraCompra(new SinDescuento(), 100000);
        assertThat(descuento.aplicar(costo)).isZero();
    }

    @Test
    void laFabricaValidaLosCodigos() {
        FabricaDescuentos fabrica = new FabricaDescuentos();
        assertThat(fabrica.crear(List.of("domi20"), costo, false).aplicar(costo)).isEqualTo(45000);
        assertThatThrownBy(() -> fabrica.crear(List.of("BIENVENIDA"), costo, false))
                .isInstanceOf(ReglaNegocioException.class);
        assertThatThrownBy(() -> fabrica.crear(List.of("NOEXISTE"), costo, true))
                .isInstanceOf(ReglaNegocioException.class);
    }
}
