package co.edu.uniquindio.cinemauq.servicio.administracion;

import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.servicio.reportes.ExportadorReporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.IndicadoresOperacion;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;

import java.io.File;
import java.util.List;

public interface ServicioAdministracion {

    List<Cliente> listarClientes();

    List<SolicitudRecarga> solicitudesPendientes();

    void recargarTarjeta(Cliente cliente, long valor);

    void aprobarSolicitud(SolicitudRecarga solicitud);

    void rechazarSolicitud(SolicitudRecarga solicitud);

    void bloquearTarjeta(Cliente cliente);

    void activarTarjeta(Cliente cliente);

    List<Funcion> listarFunciones();

    List<Reembolso> cancelarFuncion(Funcion funcion);

    Reporte generarReporte(TipoReporte tipo);

    void exportarReporte(Reporte reporte, ExportadorReporte exportador, File destino);

    IndicadoresOperacion consultarIndicadores();
}