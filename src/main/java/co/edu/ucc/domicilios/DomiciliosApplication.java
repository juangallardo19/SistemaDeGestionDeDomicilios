package co.edu.ucc.domicilios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la Plataforma de Domicilios.
 *
 * Al iniciar:
 *  1. {@link co.edu.ucc.domicilios.config.DatosIniciales} carga establecimientos, productos,
 *     clientes y repartidores de ejemplo.
 *  2. {@link co.edu.ucc.domicilios.config.DemoConsola} ejecuta por consola el flujo completo
 *     de un pedido (pedido, pago, preparación, asignación, seguimiento y entrega).
 *  3. La API REST queda disponible en http://localhost:8080/api para probarla con Postman.
 */
@SpringBootApplication
public class DomiciliosApplication {

    public static void main(String[] args) {
        SpringApplication.run(DomiciliosApplication.class, args);
    }
}
