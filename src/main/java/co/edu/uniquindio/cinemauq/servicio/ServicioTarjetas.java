package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.excepciones.OperacionNoAutorizadaException;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoSolicitud;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Administrador;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;
import co.edu.uniquindio.cinemauq.util.Formatos;

import java.util.ArrayList;
import java.util.List;

public class ServicioTarjetas {

    public static final long RECARGA_MINIMA = 10_000;
    public static final long RECARGA_MAXIMA = 500_000;

    private final Repositorio<SolicitudRecarga> solicitudes;
    private final Reloj reloj;

    public ServicioTarjetas(Repositorio<SolicitudRecarga> solicitudes, Reloj reloj) {
        this.solicitudes = solicitudes;
        this.reloj = reloj;
    }

    public SolicitudRecarga solicitarRecarga(Cliente cliente, long valor) {
        validarValor(valor);
        SolicitudRecarga solicitud = new SolicitudRecarga(cliente, valor, reloj.ahora());
        solicitudes.guardar(solicitud);
        return solicitud;
    }

    public void recargar(Administrador admin, Cliente cliente, long valor) {
        validarAdmin(admin);
        validarValor(valor);
        cliente.getTarjeta().recargar(valor, reloj.ahora(), "Recarga realizada por " + admin.getNombre());
    }

    public void aprobarSolicitud(Administrador admin, SolicitudRecarga solicitud) {
        validarAdmin(admin);
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new DatoInvalidoException("La solicitud " + solicitud.getCodigo() + " ya fue atendida");
        }
        recargar(admin, solicitud.getCliente(), solicitud.getValor());
        solicitud.aprobar(admin);
    }

    public void rechazarSolicitud(Administrador admin, SolicitudRecarga solicitud) {
        validarAdmin(admin);
        solicitud.rechazar(admin);
    }

    public List<SolicitudRecarga> solicitudesPendientes() {
        List<SolicitudRecarga> pendientes = new ArrayList<>();
        for (SolicitudRecarga solicitud : solicitudes.listar()) {
            if (solicitud.getEstado() == EstadoSolicitud.PENDIENTE) {
                pendientes.add(solicitud);
            }
        }
        return pendientes;
    }

    public void bloquearTarjeta(Administrador admin, Cliente cliente) {
        validarAdmin(admin);
        cliente.getTarjeta().bloquear();
    }

    public void activarTarjeta(Administrador admin, Cliente cliente) {
        validarAdmin(admin);
        cliente.getTarjeta().activar();
    }

    private void validarValor(long valor) {
        if (valor < RECARGA_MINIMA || valor > RECARGA_MAXIMA) {
            throw new DatoInvalidoException("La recarga debe estar entre " + Formatos.pesos(RECARGA_MINIMA)
                    + " y " + Formatos.pesos(RECARGA_MAXIMA));
        }
    }

    private void validarAdmin(Administrador admin) {
        if (admin == null) {
            throw new OperacionNoAutorizadaException("Solo un administrador puede realizar esta operación");
        }
    }
}
