package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.OperacionNoAutorizadaException;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Administrador;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;

public class SesionUsuario {

    private Usuario usuarioActual;

    public void iniciar(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrar() {
        this.usuarioActual = null;
    }

    public boolean hayUsuario() {
        return usuarioActual != null;
    }

    public boolean esAdministrador() {
        return usuarioActual instanceof Administrador;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Cliente getCliente() {
        if (!(usuarioActual instanceof Cliente)) {
            throw new OperacionNoAutorizadaException("Debe iniciar sesión como cliente");
        }
        return (Cliente) usuarioActual;
    }

    public Administrador getAdministrador() {
        if (!(usuarioActual instanceof Administrador)) {
            throw new OperacionNoAutorizadaException("Debe iniciar sesión como administrador");
        }
        return (Administrador) usuarioActual;
    }
}
