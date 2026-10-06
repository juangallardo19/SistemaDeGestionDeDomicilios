package co.edu.ucc.domicilios.patrones;

import co.edu.ucc.domicilios.dominio.ubicacion.Ruta;
import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;
import co.edu.ucc.domicilios.dominio.usuario.Repartidor;
import co.edu.ucc.domicilios.patrones.asignacion.AsignacionMasCercano;
import co.edu.ucc.domicilios.patrones.asignacion.AsignacionMenosOcupado;
import co.edu.ucc.domicilios.patrones.tiempo.CalculadorPorVelocidadPromedio;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EstrategiasTest {

    private final Ubicacion establecimiento = new Ubicacion(4.6680, -74.0530);

    @Test
    void masCercanoEscogeElDeMenorDistancia() {
        Repartidor cerca = new Repartidor("Cerca", "c@x.com", "1", "Moto", 30, new Ubicacion(4.6685, -74.0530));
        Repartidor lejos = new Repartidor("Lejos", "l@x.com", "2", "Moto", 30, new Ubicacion(4.6900, -74.0300));

        assertThat(new AsignacionMasCercano().seleccionar(List.of(lejos, cerca), establecimiento)).contains(cerca);
    }

    @Test
    void menosOcupadoEscogeElDeMenosEntregas() {
        Repartidor cerca = new Repartidor("Cerca", "c@x.com", "1", "Moto", 30, new Ubicacion(4.6685, -74.0530));
        Repartidor lejos = new Repartidor("Lejos", "l@x.com", "2", "Moto", 30, new Ubicacion(4.6900, -74.0300));
        cerca.registrarEntrega();

        assertThat(new AsignacionMenosOcupado().seleccionar(List.of(cerca, lejos), establecimiento)).contains(lejos);
    }

    @Test
    void elTiempoEstimadoBajaMientrasAvanzaLaRuta() {
        Ruta ruta = Ruta.trazar(establecimiento, new Ubicacion(4.6865, -74.0510));
        CalculadorPorVelocidadPromedio calculador = new CalculadorPorVelocidadPromedio();

        int alInicio = calculador.minutosEstimados(ruta, 20);
        for (int i = 0; i < ruta.getPuntos().size() / 2; i++) {
            ruta.avanzar();
        }
        int aMitad = calculador.minutosEstimados(ruta, 20);
        while (ruta.avanzar()) {
            // llegar al destino
        }

        assertThat(alInicio).isGreaterThan(aMitad);
        assertThat(calculador.minutosEstimados(ruta, 20)).isZero();
        assertThat(ruta.progreso()).isEqualTo(100);
    }
}
