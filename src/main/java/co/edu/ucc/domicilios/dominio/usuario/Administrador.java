package co.edu.ucc.domicilios.dominio.usuario;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administradores")
public class Administrador extends Usuario {

    private String area;

    protected Administrador() {
    }

    public Administrador(String nombre, String correo, String telefono, String area) {
        super(nombre, correo, telefono);
        this.area = area;
    }

    @Override
    public Rol getRol() {
        return Rol.ADMINISTRADOR;
    }

    public String getArea() {
        return area;
    }
}
