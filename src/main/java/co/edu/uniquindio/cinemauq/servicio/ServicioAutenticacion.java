package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.AutenticacionException;
import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.excepciones.EntidadNoEncontradaException;
import co.edu.uniquindio.cinemauq.excepciones.RegistroDuplicadoException;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Administrador;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;

import java.util.ArrayList;
import java.util.List;

public class ServicioAutenticacion {

    public static final int LONGITUD_MINIMA_CONTRASENA = 6;

    private final Repositorio<Usuario> usuarios;

    public ServicioAutenticacion(Repositorio<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Cliente registrarCliente(String cedula, String nombre, String correo, String contrasena) {
        validarDatosRegistro(cedula, correo, contrasena);
        Cliente cliente = new Cliente(cedula, nombre, correo, contrasena);
        usuarios.guardar(cliente);
        return cliente;
    }

    public Administrador registrarAdministrador(String cedula, String nombre, String correo, String contrasena) {
        validarDatosRegistro(cedula, correo, contrasena);
        Administrador admin = new Administrador(cedula, nombre, correo, contrasena);
        usuarios.guardar(admin);
        return admin;
    }

    public Usuario iniciarSesion(String correo, String contrasena) {
        Usuario usuario = usuarios.buscar(correo);
        if (usuario == null || !usuario.verificarContrasena(contrasena)) {
            throw new AutenticacionException("Correo o contraseña incorrectos");
        }
        return usuario;
    }

    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        for (Usuario usuario : usuarios.listar()) {
            if (usuario instanceof Cliente) {
                clientes.add((Cliente) usuario);
            }
        }
        return clientes;
    }

    public Cliente buscarCliente(String correo) {
        Usuario usuario = usuarios.buscar(correo);
        if (!(usuario instanceof Cliente)) {
            throw new EntidadNoEncontradaException("No existe el cliente " + correo);
        }
        return (Cliente) usuario;
    }

    private void validarDatosRegistro(String cedula, String correo, String contrasena) {
        if (!esCedulaValida(cedula)) {
            throw new DatoInvalidoException("La cédula debe tener entre 5 y 12 dígitos");
        }
        if (!esCorreoValido(correo)) {
            throw new DatoInvalidoException("El correo no tiene un formato válido");
        }
        if (contrasena == null || contrasena.length() < LONGITUD_MINIMA_CONTRASENA) {
            throw new DatoInvalidoException("La contraseña debe tener al menos " + LONGITUD_MINIMA_CONTRASENA + " caracteres");
        }
        if (usuarios.existe(correo)) {
            throw new RegistroDuplicadoException("Ya existe un usuario con el correo " + correo);
        }
        for (Usuario usuario : usuarios.listar()) {
            if (usuario.getCedula().equals(cedula.trim())) {
                throw new RegistroDuplicadoException("Ya existe un usuario con la cédula " + cedula);
            }
        }
    }

    private boolean esCedulaValida(String cedula) {
        if (cedula == null) {
            return false;
        }
        String limpia = cedula.trim();
        if (limpia.length() < 5 || limpia.length() > 12) {
            return false;
        }
        for (int i = 0; i < limpia.length(); i++) {
            if (!Character.isDigit(limpia.charAt(i))) {
                return false;
            }
        }
        return true;
    }

   private boolean esCorreoValido(String correo) {
        if (correo == null || correo.contains(" ")) {
            return false;
        }
        int posicionArroba = correo.indexOf('@');
        if (posicionArroba <= 0 || posicionArroba != correo.lastIndexOf('@')) {
            return false;
        }
        String dominio = correo.substring(posicionArroba + 1);
        int posicionPunto = dominio.lastIndexOf('.');
        return posicionPunto > 0 && posicionPunto < dominio.length() - 1;
    }
}
