package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.servicio.administracion.ServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.eventos.EstadisticasOperacion;
import co.edu.uniquindio.cinemauq.servicio.reportes.ExportadorReporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.IndicadoresOperacion;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TextoReporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;
import co.edu.uniquindio.cinemauq.util.Formatos;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminControlador extends ControladorBase {

    private static final DateTimeFormatter FORMATO_DIA = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter FORMATO_ARCHIVO = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

    @FXML
    private Label lblBienvenida;
    @FXML
    private ListView<Cliente> lstClientes;
    @FXML
    private Label lblDetalleCliente;
    @FXML
    private TextField txtValorRecarga;
    @FXML
    private ListView<SolicitudRecarga> lstSolicitudes;
    @FXML
    private ListView<Funcion> lstFunciones;
    @FXML
    private ListView<String> lstBitacora;

    @FXML
    private FlowPane panelIndicadores;
    @FXML
    private BarChart<String, Number> graficoIngresos;
    @FXML
    private PieChart graficoEntradas;
    @FXML
    private LineChart<String, Number> graficoVentasDia;
    @FXML
    private BarChart<String, Number> graficoOcupacion;
    @FXML
    private PieChart graficoConfiteria;
    @FXML
    private ListView<String> lstActividad;

    @FXML
    private ComboBox<TipoReporte> cmbTipoReporte;
    @FXML
    private ComboBox<ExportadorReporte> cmbFormato;
    @FXML
    private TextArea txtReporte;

    private ServicioAdministracion administracion;
    private Reporte reporteActual;

    @Override
    protected void alCargar() {
        administracion = fachada.administracion();
        lblBienvenida.setText("Administrador: " + fachada.getUsuarioActual().getNombre());
        cmbTipoReporte.getItems().setAll(TipoReporte.values());
        cmbTipoReporte.getSelectionModel().selectFirst();
        cmbFormato.getItems().setAll(fachada.exportadoresDisponibles());
        cmbFormato.setConverter(new StringConverter<ExportadorReporte>() {
            @Override
            public String toString(ExportadorReporte exportador) {
                if (exportador == null) {
                    return "";
                }
                return exportador.getNombre() + " (." + exportador.getExtension() + ")";
            }

            @Override
            public ExportadorReporte fromString(String texto) {
                return null;
            }
        });
        cmbFormato.getSelectionModel().selectFirst();
        refrescar();
        generarReporte();
    }

    @FXML
    private void actualizarEstadisticas() {
        try {
            mostrarEstadisticas(administracion.consultarIndicadores());
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    private void mostrarEstadisticas(IndicadoresOperacion indicadores) {
        EstadisticasOperacion estadisticas = indicadores.getEstadisticas();
        panelIndicadores.getChildren().setAll(
                indicador(Formatos.pesos(estadisticas.getValorVendido()), "Valor vendido"),
                indicador(Formatos.pesos(estadisticas.getIngresoNeto()), "Ingreso neto"),
                indicador(String.valueOf(estadisticas.getComprasConfirmadas()), "Compras confirmadas"),
                indicador(String.valueOf(estadisticas.getEntradasVendidas()), "Entradas vendidas"),
                indicador(Formatos.pesos(estadisticas.getTicketPromedio()), "Ticket promedio"),
                indicador(String.valueOf(estadisticas.getComprasCanceladas()), "Compras canceladas"),
                indicador(Formatos.pesos(estadisticas.getValorReembolsado()), "Valor reembolsado"),
                indicador(String.format(Locale.ROOT, "%.1f %%", estadisticas.getTasaCancelacion()), "Tasa de cancelación"),
                indicador(String.valueOf(estadisticas.getFuncionesCanceladas()), "Funciones canceladas"),
                indicador(Formatos.pesos(estadisticas.getValorRecargado()), "Recargado a tarjetas"));

        XYChart.Series<String, Number> ingresos = new XYChart.Series<>();
        for (Map.Entry<String, Long> pelicula : indicadores.getIngresosPorPelicula().entrySet()) {
            ingresos.getData().add(new XYChart.Data<>(pelicula.getKey(), pelicula.getValue()));
        }
        graficoIngresos.getData().clear();
        graficoIngresos.getData().add(ingresos);

        graficoEntradas.getData().clear();
        for (Map.Entry<String, Integer> tipo : indicadores.getEntradasPorTipo().entrySet()) {
            if (tipo.getValue() > 0) {
                graficoEntradas.getData().add(new PieChart.Data(tipo.getKey() + " (" + tipo.getValue() + ")", tipo.getValue()));
            }
        }

        XYChart.Series<String, Number> ventas = new XYChart.Series<>();
        ventas.setName("Ventas");
        for (Map.Entry<LocalDate, Long> dia : indicadores.getVentasPorDia().entrySet()) {
            ventas.getData().add(new XYChart.Data<>(dia.getKey().format(FORMATO_DIA), dia.getValue()));
        }
        XYChart.Series<String, Number> reembolsos = new XYChart.Series<>();
        reembolsos.setName("Reembolsos");
        for (Map.Entry<LocalDate, Long> dia : indicadores.getReembolsosPorDia().entrySet()) {
            reembolsos.getData().add(new XYChart.Data<>(dia.getKey().format(FORMATO_DIA), dia.getValue()));
        }
        graficoVentasDia.getData().clear();
        graficoVentasDia.getData().add(ventas);
        graficoVentasDia.getData().add(reembolsos);

        XYChart.Series<String, Number> ocupacion = new XYChart.Series<>();
        for (Map.Entry<String, Double> sala : indicadores.getOcupacionPorSala().entrySet()) {
            ocupacion.getData().add(new XYChart.Data<>(sala.getKey(), Math.round(sala.getValue() * 10) / 10.0));
        }
        graficoOcupacion.getData().clear();
        graficoOcupacion.getData().add(ocupacion);

        graficoConfiteria.getData().clear();
        for (Map.Entry<String, Long> producto : indicadores.getUnidadesConfiteria().entrySet()) {
            graficoConfiteria.getData().add(new PieChart.Data(producto.getKey() + " (" + producto.getValue() + ")",
                    producto.getValue()));
        }

        lstActividad.getItems().setAll(indicadores.getActividadReciente());
    }

    private VBox indicador(String valor, String nombre) {
        Label lblValor = new Label(valor);
        lblValor.getStyleClass().add("indicador-valor");
        Label lblNombre = new Label(nombre);
        lblNombre.getStyleClass().add("indicador-nombre");
        VBox caja = new VBox(2, lblValor, lblNombre);
        caja.getStyleClass().add("indicador");
        return caja;
    }

    @FXML
    private void generarReporte() {
        TipoReporte tipo = cmbTipoReporte.getValue();
        if (tipo == null) {
            mostrarError("Seleccione el tipo de reporte");
            return;
        }
        try {
            reporteActual = administracion.generarReporte(tipo);
            txtReporte.setText(TextoReporte.completo(reporteActual));
            lstBitacora.getItems().setAll(fachada.consultarBitacora());
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void exportarReporte() {
        ExportadorReporte exportador = cmbFormato.getValue();
        if (exportador == null) {
            mostrarError("Seleccione el formato de exportación");
            return;
        }
        if (reporteActual == null || reporteActual.getTipo() != cmbTipoReporte.getValue()) {
            generarReporte();
        }
        if (reporteActual == null) {
            return;
        }
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar reporte");
        selector.setInitialFileName(nombreArchivo(reporteActual) + "." + exportador.getExtension());
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter(exportador.getNombre(),
                "*." + exportador.getExtension()));
        File destino = selector.showSaveDialog(txtReporte.getScene().getWindow());
        if (destino == null) {
            return;
        }
        try {
            administracion.exportarReporte(reporteActual, exportador, destino);
            mostrarInfo("Reporte guardado en:\n" + destino.getAbsolutePath());
            lstBitacora.getItems().setAll(fachada.consultarBitacora());
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    private String nombreArchivo(Reporte reporte) {
        return "CinemaUQ_" + reporte.getTipo().name().toLowerCase() + "_"
                + reporte.getGeneradoEn().format(FORMATO_ARCHIVO);
    }

    @FXML
    private void verCliente() {
        Cliente cliente = lstClientes.getSelectionModel().getSelectedItem();
        if (cliente == null) {
            lblDetalleCliente.setText("");
            return;
        }
        String estado = "activa";
        if (!cliente.getTarjeta().isActiva()) {
            estado = "bloqueada";
        }
        lblDetalleCliente.setText("Tarjeta " + cliente.getTarjeta().getNumero() + " (" + estado + ")   saldo: "
                + Formatos.pesos(cliente.getTarjeta().getSaldo()) + "   compras: " + cliente.getCompras().size());
    }

    private Cliente clienteSeleccionado() {
        Cliente cliente = lstClientes.getSelectionModel().getSelectedItem();
        if (cliente == null) {
            mostrarError("Seleccione un cliente");
        }
        return cliente;
    }

    @FXML
    private void recargar() {
        Cliente cliente = clienteSeleccionado();
        if (cliente == null) {
            return;
        }
        try {
            long valor = leerValor(txtValorRecarga.getText());
            administracion.recargarTarjeta(cliente, valor);
            txtValorRecarga.clear();
            mostrarInfo("Se recargaron " + Formatos.pesos(valor) + " a " + cliente.getNombre());
            refrescar();
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void bloquearTarjeta() {
        Cliente cliente = clienteSeleccionado();
        if (cliente == null) {
            return;
        }
        try {
            administracion.bloquearTarjeta(cliente);
            refrescar();
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void activarTarjeta() {
        Cliente cliente = clienteSeleccionado();
        if (cliente == null) {
            return;
        }
        try {
            administracion.activarTarjeta(cliente);
            refrescar();
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void aprobarSolicitud() {
        SolicitudRecarga solicitud = lstSolicitudes.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            mostrarError("Seleccione una solicitud");
            return;
        }
        try {
            administracion.aprobarSolicitud(solicitud);
            mostrarInfo("Solicitud " + solicitud.getCodigo() + " aprobada");
            refrescar();
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void rechazarSolicitud() {
        SolicitudRecarga solicitud = lstSolicitudes.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            mostrarError("Seleccione una solicitud");
            return;
        }
        try {
            administracion.rechazarSolicitud(solicitud);
            refrescar();
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void cancelarFuncion() {
        Funcion funcion = lstFunciones.getSelectionModel().getSelectedItem();
        if (funcion == null) {
            mostrarError("Seleccione una función");
            return;
        }
        if (!confirmar("Cancelar función " + funcion.getCodigo(), "Se devolverá el 100 % a todos los compradores. ¿Continuar?")) {
            return;
        }
        try {
            List<Reembolso> reembolsos = administracion.cancelarFuncion(funcion);
            long total = 0;
            for (Reembolso reembolso : reembolsos) {
                total = total + reembolso.getValor();
            }
            mostrarInfo("Función cancelada. Reembolsos: " + reembolsos.size() + " por " + Formatos.pesos(total));
            refrescar();
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void cerrarSesion() {
        navegador.irALogin();
    }

    private void refrescar() {
        lstClientes.getItems().setAll(administracion.listarClientes());
        lblDetalleCliente.setText("");
        lstSolicitudes.getItems().setAll(administracion.solicitudesPendientes());
        lstFunciones.getItems().setAll(administracion.listarFunciones());
        lstBitacora.getItems().setAll(fachada.consultarBitacora());
        mostrarEstadisticas(administracion.consultarIndicadores());
    }
}