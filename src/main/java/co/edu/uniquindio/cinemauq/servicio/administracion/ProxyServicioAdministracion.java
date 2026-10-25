package co.edu.uniquindio.cinemauq.servicio.administracion;

import co.edu.uniquindio.cinemauq.excepciones.OperacionNoAutorizadaException;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.servicio.Reloj;
import co.edu.uniquindio.cinemauq.servicio.SesionUsuario;
import co.edu.uniquindio.cinemauq.util.Formatos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProxyServicioAdministracion implements ServicioAdministracion {

    private final ServicioAdministracion servicioReal;
    private final SesionUsuario sesion;
    private final Reloj reloj;
    private final List<String> bitacora = new ArrayList<>();

    public ProxyServicioAdministracion(ServicioAdministracion servicioReal, SesionUsuario sesion, Reloj reloj) {
        this.servicioReal = servicioReal;
        this.sesion = sesion;
        this.reloj = reloj;
    }

    private void verificarAcceso(String operacion) {
        if (!sesion.esAdministrador()) {
            String quien = "sin sesión";
            if (sesion.hayUsuario()) {
                quien = sesion.getUsuarioActual().getCorreo();
            }
            bitacora.add(reloj.ahora().format(Formatos.FECHA_HORA) + " - DENEGADO - " + quien + " - " + operacion);
            throw new OperacionNoAutorizadaException("Solo un administrador puede " + operacion);
        }
    }

    private void registrar(String operacion) {
        bitacora.add(reloj.ahora().format(Formatos.FECHA_HORA) + " - " + sesion.getUsuarioActual().getCorreo() + " - " + operacion);
    }

    @Override
    public List<Cliente> listarClientes() {
        verificarAcceso("consultar clientes");
        return servicioReal.listarClientes();
    }

    @Override
    public List<SolicitudRecarga> solicitudesPendientes() {
        verificarAcceso("consultar solicitudes de recarga");
        return servicioReal.solicitudesPendientes();
    }

    @Override
    public void recargarTarjeta(Cliente cliente, long valor) {
        verificarAcceso("recargar tarjetas");
        servicioReal.recargarTarjeta(cliente, valor);
        registrar("recargó " + Formatos.pesos(valor) + " a " + cliente.getCorreo());
    }

    @Override
    public void aprobarSolicitud(SolicitudRecarga solicitud) {
        verificarAcceso("aprobar solicitudes de recarga");
        servicioReal.aprobarSolicitud(solicitud);
        registrar("aprobó la solicitud " + solicitud.getCodigo());
    }

    @Override
    public void rechazarSolicitud(SolicitudRecarga solicitud) {
        verificarAcceso("rechazar solicitudes de recarga");
        servicioReal.rechazarSolicitud(solicitud);
        registrar("rechazó la solicitud " + solicitud.getCodigo());
    }

    @Override
    public void bloquearTarjeta(Cliente cliente) {
        verificarAcceso("bloquear tarjetas");
        servicioReal.bloquearTarjeta(cliente);
        registrar("bloqueó la tarjeta de " + cliente.getCorreo());
    }

    @Override
    public void activarTarjeta(Cliente cliente) {
        verificarAcceso("activar tarjetas");
        servicioReal.activarTarjeta(cliente);
        registrar("activó la tarjeta de " + cliente.getCorreo());
    }

    @Override
    public List<Funcion> listarFunciones() {
        verificarAcceso("consultar funciones");
        return servicioReal.listarFunciones();
    }

    @Override
    public List<Reembolso> cancelarFuncion(Funcion funcion) {
        verificarAcceso("cancelar funciones");
        List<Reembolso> reembolsos = servicioReal.cancelarFuncion(funcion);
        registrar("canceló la función " + funcion.getCodigo() + " con " + reembolsos.size() + " reembolso(s)");
        return reembolsos;
    }

    public List<String> getBitacora() {
        return Collections.unmodifiableList(bitacora);
    }
}
