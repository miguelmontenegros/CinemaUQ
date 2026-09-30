package co.edu.uniquindio.cinemauq.modelo.tarjeta;

import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.modelo.GeneradorCodigos;
import co.edu.uniquindio.cinemauq.modelo.contratos.Identificable;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoSolicitud;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoCodigo;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Administrador;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;

import java.time.LocalDateTime;

public class SolicitudRecarga implements Identificable {

    private final String codigo;
    private final Cliente cliente;
    private final long valor;
    private final LocalDateTime fecha;
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;
    private Administrador atendidaPor;

    public SolicitudRecarga(Cliente cliente, long valor, LocalDateTime fecha) {
        this.codigo = GeneradorCodigos.getInstancia().siguiente(TipoCodigo.SOLICITUD_RECARGA);
        this.cliente = cliente;
        this.valor = valor;
        this.fecha = fecha;
    }

    public void aprobar(Administrador admin) {
        cerrar(admin, EstadoSolicitud.APROBADA);
    }

    public void rechazar(Administrador admin) {
        cerrar(admin, EstadoSolicitud.RECHAZADA);
    }

    private void cerrar(Administrador admin, EstadoSolicitud nuevoEstado) {
        if (estado != EstadoSolicitud.PENDIENTE) {
            throw new DatoInvalidoException("La solicitud " + codigo + " ya fue atendida");
        }
        this.atendidaPor = admin;
        this.estado = nuevoEstado;
    }

    @Override
    public String getIdentificador() {
        return codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public long getValor() {
        return valor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public Administrador getAtendidaPor() {
        return atendidaPor;
    }
}
