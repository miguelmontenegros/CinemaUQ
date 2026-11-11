package co.edu.uniquindio.cinemauq.servicio.administracion;

import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.excepciones.ExportacionReporteException;
import co.edu.uniquindio.cinemauq.servicio.*;
import co.edu.uniquindio.cinemauq.servicio.eventos.RegistroActividad;
import co.edu.uniquindio.cinemauq.servicio.reportes.ExportadorReporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.GeneradorReportes;
import co.edu.uniquindio.cinemauq.servicio.reportes.IndicadoresOperacion;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ServicioAdministracionReal implements ServicioAdministracion {

    private static final int CANTIDAD_ACTIVIDAD = 40;

    private final ServicioAutenticacion autenticacion;
    private final ServicioTarjetas tarjetas;
    private final ServicioCartelera cartelera;
    private final ServicioCancelaciones cancelaciones;
    private final SesionUsuario sesion;
    private final GeneradorReportes reportes;
    private final RegistroActividad actividad;

    public ServicioAdministracionReal(ServicioAutenticacion autenticacion, ServicioTarjetas tarjetas,
                                      ServicioCartelera cartelera, ServicioCancelaciones cancelaciones,
                                      SesionUsuario sesion, GeneradorReportes reportes, RegistroActividad actividad) {
        this.autenticacion = autenticacion;
        this.tarjetas = tarjetas;
        this.cartelera = cartelera;
        this.cancelaciones = cancelaciones;
        this.sesion = sesion;
        this.reportes = reportes;
        this.actividad = actividad;
    }

    @Override
    public Reporte generarReporte(TipoReporte tipo) {
        return reportes.generar(tipo);
    }

    @Override
    public void exportarReporte(Reporte reporte, ExportadorReporte exportador, File destino) {
        try {
            exportador.exportar(reporte, destino);
        } catch (IOException e) {
            throw new ExportacionReporteException("No se pudo guardar el reporte en " + destino.getName()
                    + ": " + e.getMessage());
        }
    }

    @Override
    public IndicadoresOperacion consultarIndicadores() {
        return new IndicadoresOperacion(reportes, actividad.ultimas(CANTIDAD_ACTIVIDAD));
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