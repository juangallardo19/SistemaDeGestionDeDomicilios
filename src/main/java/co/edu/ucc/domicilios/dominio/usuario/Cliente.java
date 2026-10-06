package co.edu.ucc.domicilios.dominio.usuario;

import co.edu.ucc.domicilios.dominio.ubicacion.Ubicacion;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente extends Usuario {

    private String direccion;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitud", column = @Column(name = "direccion_latitud")),
            @AttributeOverride(name = "longitud", column = @Column(name = "direccion_longitud"))
    })
    private Ubicacion ubicacion;

    protected Cliente() {
    }

    public Cliente(String nombre, String correo, String telefono, String direccion, Ubicacion ubicacion) {
        super(nombre, correo, telefono);
        this.direccion = direccion;
        this.ubicacion = ubicacion;
    }

    @Override
    public Rol getRol() {
        return Rol.CLIENTE;
    }

    public void cambiarDireccion(String direccion, Ubicacion ubicacion) {
        this.direccion = direccion;
        this.ubicacion = ubicacion;
    }

    public String getDireccion() {
        return direccion;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }
}
