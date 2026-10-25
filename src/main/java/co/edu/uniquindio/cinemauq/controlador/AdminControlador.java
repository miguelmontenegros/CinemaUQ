package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.servicio.administracion.ServicioAdministracion;
import co.edu.uniquindio.cinemauq.util.Formatos;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;

public class AdminControlador extends ControladorBase {

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

    private ServicioAdministracion administracion;

    @Override
    protected void alCargar() {
        administracion = fachada.administracion();
        lblBienvenida.setText("Administrador: " + fachada.getUsuarioActual().getNombre());
        refrescar();
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
    }
}
