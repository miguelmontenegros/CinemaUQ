package co.edu.uniquindio.cinemauq.modelo.usuarios;



import co.edu.uniquindio.cinemauq.modelo.contratos.Identificable;
import co.edu.uniquindio.cinemauq.util.Seguridad;
import co.edu.uniquindio.cinemauq.util.Validaciones;


public abstract class Usuario implements Identificable {

    private final String cedula;
    private final String nombre;
    private final String correo;
    private final String hashContrasena;

    protected Usuario(String cedula, String nombre, String correo, String contrasena) {
        this.cedula = Validaciones.texto(cedula, "cédula");
        this.nombre = Validaciones.texto(nombre, "nombre");
        this.correo = Validaciones.texto(correo, "correo").toLowerCase();
        this.hashContrasena = Seguridad.hash(Validaciones.texto(contrasena, "contraseña"));
    }

    public boolean verificarContrasena(String contrasena) {
        return contrasena != null && hashContrasena.equals(Seguridad.hash(contrasena));
    }

    public abstract String getRol();

    @Override
    public String getIdentificador() {
        return correo;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    @Override
    public String toString() {
        return nombre + " <" + correo + ">";
    }
}
