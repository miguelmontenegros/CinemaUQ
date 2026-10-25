package co.edu.uniquindio.cinemauq.servicio.administracion;

import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.servicio.*;

import java.util.List;

public class ServicioAdministracionReal implements ServicioAdministracion {

    private final ServicioAutenticacion autenticacion;
    private final ServicioTarjetas tarjetas;
    private final ServicioCartelera cartelera;
    private final ServicioCancelaciones cancelaciones;
    private final SesionUsuario sesion;

    public ServicioAdministracionReal(ServicioAutenticacion autenticacion, ServicioTarjetas tarjetas,
                                      ServicioCartelera cartelera, ServicioCancelaciones cancelaciones,
                                      SesionUsuario sesion) {
        this.autenticacion = autenticacion;
        this.tarjetas = tarjetas;
        this.cartelera = cartelera;
        this.cancelaciones = cancelaciones;
        this.sesion = sesion;
    }

    @Override
    public List<Cliente> listarClientes() {
        return autenticacion.listarClientes();
    }

    @Override
    public List<SolicitudRecarga> solicitudesPendientes() {
        return tarjetas.solicitudesPendientes();
    }

    @Override
    public void recargarTarjeta(Cliente cliente, long valor) {
        tarjetas.recargar(sesion.getAdministrador(), cliente, valor);
    }

    @Override
    public void aprobarSolicitud(SolicitudRecarga solicitud) {
        tarjetas.aprobarSolicitud(sesion.getAdministrador(), solicitud);
    }

    @Override
    public void rechazarSolicitud(SolicitudRecarga solicitud) {
        tarjetas.rechazarSolicitud(sesion.getAdministrador(), solicitud);
    }

    @Override
    public void bloquearTarjeta(Cliente cliente) {
        tarjetas.bloquearTarjeta(sesion.getAdministrador(), cliente);
    }

    @Override
    public void activarTarjeta(Cliente cliente) {
        tarjetas.activarTarjeta(sesion.getAdministrador(), cliente);
    }

    @Override
    public List<Funcion> listarFunciones() {
        return cartelera.listarFunciones();
    }

    @Override
    public List<Reembolso> cancelarFuncion(Funcion funcion) {
        return cancelaciones.cancelarFuncion(sesion.getAdministrador(), funcion);
    }
}
