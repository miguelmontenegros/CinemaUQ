package co.edu.uniquindio.cinemauq.modelo.usuarios;


public class Administrador extends Usuario {

    public Administrador(String cedula, String nombre, String correo, String contrasena) {
        super(cedula, nombre, correo, contrasena);
    }

    @Override
    public String getRol() {
        return "ADMINISTRADOR";
    }
}