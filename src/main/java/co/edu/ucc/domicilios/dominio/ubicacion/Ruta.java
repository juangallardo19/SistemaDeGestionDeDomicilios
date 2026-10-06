package co.edu.ucc.domicilios.dominio.ubicacion;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Secuencia de puntos que recorre el repartidor desde el establecimiento hasta la
 * dirección del cliente. En la versión académica la ruta se traza "por calles"
 * (tramos norte-sur y oriente-occidente) y el avance se simula punto a punto.
 */
@Entity
@Table(name = "rutas")
public class Ruta {

    /** Separación aproximada entre puntos de la ruta (~150 m). */
    private static final double PASO_GRADOS = 0.0014;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "ruta_puntos")
    @OrderColumn(name = "orden")
    private List<Ubicacion> puntos = new ArrayList<>();

    private int indiceActual;

    protected Ruta() {
    }

    public Ruta(List<Ubicacion> puntos) {
        if (puntos == null || puntos.size() < 2) {
            throw new IllegalArgumentException("Una ruta necesita al menos origen y destino");
        }
        this.puntos = new ArrayList<>(puntos);
        this.indiceActual = 0;
    }

    /** Traza una ruta en forma de "Z": media distancia en latitud, luego longitud y luego el resto de latitud. */
    public static Ruta trazar(Ubicacion origen, Ubicacion destino) {
        double latitudMedia = (origen.getLatitud() + destino.getLatitud()) / 2;
        List<Ubicacion> puntos = new ArrayList<>();
        puntos.add(origen);
        agregarTramo(puntos, new Ubicacion(latitudMedia, origen.getLongitud()));
        agregarTramo(puntos, new Ubicacion(latitudMedia, destino.getLongitud()));
        agregarTramo(puntos, destino);
        return new Ruta(puntos);
    }

    private static void agregarTramo(List<Ubicacion> puntos, Ubicacion hasta) {
        Ubicacion desde = puntos.get(puntos.size() - 1);
        double dLat = hasta.getLatitud() - desde.getLatitud();
        double dLon = hasta.getLongitud() - desde.getLongitud();
        int pasos = (int) Math.ceil(Math.max(Math.abs(dLat), Math.abs(dLon)) / PASO_GRADOS);
        for (int i = 1; i <= pasos; i++) {
            puntos.add(new Ubicacion(desde.getLatitud() + dLat * i / pasos,
                    desde.getLongitud() + dLon * i / pasos));
        }
    }

    /** Mueve al repartidor al siguiente punto. Devuelve false si ya había llegado. */
    public boolean avanzar() {
        if (haLlegado()) {
            return false;
        }
        indiceActual++;
        return true;
    }

    /** Ubica el avance en el punto (aún no recorrido) más cercano a la posición reportada. */
    public void sincronizarCon(Ubicacion posicionReportada) {
        int masCercano = indiceActual;
        double menorDistancia = Double.MAX_VALUE;
        for (int i = indiceActual; i < puntos.size(); i++) {
            double distancia = puntos.get(i).distanciaKmA(posicionReportada);
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                masCercano = i;
            }
        }
        indiceActual = masCercano;
    }

    public boolean haLlegado() {
        return indiceActual >= puntos.size() - 1;
    }

    public Ubicacion posicionActual() {
        return puntos.get(indiceActual);
    }

    public Ubicacion origen() {
        return puntos.get(0);
    }

    public Ubicacion destino() {
        return puntos.get(puntos.size() - 1);
    }

    public double distanciaTotalKm() {
        return distanciaEntre(0, puntos.size() - 1);
    }

    public double distanciaRestanteKm() {
        return distanciaEntre(indiceActual, puntos.size() - 1);
    }

    /** Porcentaje recorrido (0 a 100). */
    public int progreso() {
        double total = distanciaTotalKm();
        return total == 0 ? 100 : (int) Math.round((1 - distanciaRestanteKm() / total) * 100);
    }

    private double distanciaEntre(int desde, int hasta) {
        double suma = 0;
        for (int i = desde; i < hasta; i++) {
            suma += puntos.get(i).distanciaKmA(puntos.get(i + 1));
        }
        return suma;
    }

    public Long getId() {
        return id;
    }

    public List<Ubicacion> getPuntos() {
        return Collections.unmodifiableList(puntos);
    }

    public int getIndiceActual() {
        return indiceActual;
    }
}
