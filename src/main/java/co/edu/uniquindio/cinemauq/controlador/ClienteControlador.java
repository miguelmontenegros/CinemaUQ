package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.modelo.cartelera.AsientoFuncion;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.Entrada;
import co.edu.uniquindio.cinemauq.modelo.compra.LineaConfiteria;
import co.edu.uniquindio.cinemauq.modelo.compra.Promocion;
import co.edu.uniquindio.cinemauq.modelo.compra.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.enums.AdicionalEntrada;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoAsiento;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoAsiento;
import co.edu.uniquindio.cinemauq.modelo.puntos.AcumulacionPuntos;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.Movimiento;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.util.Formatos;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class ClienteControlador extends ControladorBase {

    @FXML
    private Label lblBienvenida;
    @FXML
    private Label lblSaldoCabecera;
    @FXML
    private Label lblPuntosCabecera;

    @FXML
    private ListView<Funcion> lstFunciones;
    @FXML
    private Label lblFuncion;
    @FXML
    private GridPane gridAsientos;
    @FXML
    private Label lblSeleccion;
    @FXML
    private CheckBox chkEntradasPuntos;
    @FXML
    private CheckBox chkGafas3D;
    @FXML
    private CheckBox chkSillaVibratoria;
    @FXML
    private ComboBox<ItemConfiteria> cmbProductos;
    @FXML
    private TextField txtCantidad;
    @FXML
    private CheckBox chkProductoPuntos;
    @FXML
    private ListView<LineaConfiteria> lstCarrito;
    @FXML
    private ComboBox<Promocion> cmbPromocion;

    @FXML
    private Label lblNumeroTarjeta;
    @FXML
    private Label lblEstadoTarjeta;
    @FXML
    private Label lblSaldoTarjeta;
    @FXML
    private ListView<String> lstMovimientos;
    @FXML
    private TextField txtValorRecarga;

    @FXML
    private ListView<Compra> lstCompras;
    @FXML
    private ListView<String> lstDetalleCompra;

    @FXML
    private Label lblPuntosDisponibles;
    @FXML
    private ListView<String> lstPuntos;

    private Funcion funcionSeleccionada;
    private final List<String> asientosSeleccionados = new ArrayList<>();

    @Override
    protected void alCargar() {
        lblBienvenida.setText("Hola, " + fachada.getUsuarioActual().getNombre());
        cmbProductos.getItems().setAll(fachada.consultarConfiteria());
        txtCantidad.setText("1");
        chkGafas3D.setText(AdicionalEntrada.GAFAS_3D.getNombre() + " (+" + Formatos.pesos(AdicionalEntrada.GAFAS_3D.getValor()) + " c/u)");
        chkSillaVibratoria.setText(AdicionalEntrada.SILLA_VIBRATORIA.getNombre() + " (+"
                + Formatos.pesos(AdicionalEntrada.SILLA_VIBRATORIA.getValor()) + " c/u)");
        refrescarTodo();
    }

    @FXML
    private void seleccionarFuncion() {
        Funcion funcion = lstFunciones.getSelectionModel().getSelectedItem();
        if (funcion != null && funcion != funcionSeleccionada) {
            funcionSeleccionada = funcion;
            asientosSeleccionados.clear();
            dibujarAsientos();
        }
    }

    private void dibujarAsientos() {
        gridAsientos.getChildren().clear();
        if (funcionSeleccionada == null) {
            lblFuncion.setText("Seleccione una función de la cartelera");
            lblSeleccion.setText("");
            return;
        }
        lblFuncion.setText(funcionSeleccionada.getPelicula().getTitulo() + " - " + funcionSeleccionada.getSala()
                + " - " + funcionSeleccionada.getFechaHora().format(Formatos.FECHA_HORA)
                + "   |   General " + Formatos.pesos(funcionSeleccionada.getPrecioBase()) + ", preferencial +35 %");
        for (AsientoFuncion asientoFuncion : funcionSeleccionada.getAsientos()) {
            String codigo = asientoFuncion.getAsiento().getCodigo();
            Button boton = new Button(codigo);
            boton.getStyleClass().add("asiento");
            if (asientoFuncion.getAsiento().getTipo() == TipoAsiento.PREFERENCIAL) {
                boton.getStyleClass().add("asiento-preferencial");
            }
            if (asientoFuncion.getEstado() == EstadoAsiento.VENDIDO) {
                boton.getStyleClass().add("asiento-vendido");
                boton.setDisable(true);
            } else if (asientoFuncion.getEstado() == EstadoAsiento.BLOQUEADO) {
                boton.getStyleClass().add("asiento-bloqueado");
                boton.setDisable(true);
            } else if (asientosSeleccionados.contains(codigo)) {
                boton.getStyleClass().add("asiento-seleccionado");
            }
            boton.setOnAction(new ManejadorAsiento(codigo));
            int columna = asientoFuncion.getAsiento().getNumero() - 1;
            int fila = asientoFuncion.getAsiento().getFila() - 'A';
            gridAsientos.add(boton, columna, fila);
        }
        if (asientosSeleccionados.isEmpty()) {
            lblSeleccion.setText("No hay asientos seleccionados");
        } else {
            lblSeleccion.setText("Asientos seleccionados: " + String.join(", ", asientosSeleccionados));
        }
    }

    private class ManejadorAsiento implements EventHandler<ActionEvent> {

        private final String codigo;

        ManejadorAsiento(String codigo) {
            this.codigo = codigo;
        }

        @Override
        public void handle(ActionEvent evento) {
            if (asientosSeleccionados.contains(codigo)) {
                asientosSeleccionados.remove(codigo);
            } else {
                asientosSeleccionados.add(codigo);
            }
            dibujarAsientos();
        }
    }

    @FXML
    private void agregarProducto() {
        try {
            ItemConfiteria item = cmbProductos.getValue();
            if (item == null) {
                mostrarError("Seleccione un producto o combo");
                return;
            }
            int cantidad = (int) leerValor(txtCantidad.getText());
            if (cantidad <= 0) {
                mostrarError("La cantidad debe ser mayor que cero");
                return;
            }
            lstCarrito.getItems().add(new LineaConfiteria(item, cantidad, chkProductoPuntos.isSelected()));
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void quitarProducto() {
        LineaConfiteria linea = lstCarrito.getSelectionModel().getSelectedItem();
        if (linea != null) {
            lstCarrito.getItems().remove(linea);
        }
    }

    @FXML
    private void limpiarCompra() {
        asientosSeleccionados.clear();
        lstCarrito.getItems().clear();
        chkEntradasPuntos.setSelected(false);
        chkGafas3D.setSelected(false);
        chkSillaVibratoria.setSelected(false);
        cmbPromocion.getSelectionModel().clearSelection();
        dibujarAsientos();
    }

    @FXML
    private void comprar() {
        if (funcionSeleccionada == null) {
            mostrarError("Primero seleccione una función de la cartelera");
            return;
        }
        try {
            List<AdicionalEntrada> adicionales = new ArrayList<>();
            if (chkGafas3D.isSelected()) {
                adicionales.add(AdicionalEntrada.GAFAS_3D);
            }
            if (chkSillaVibratoria.isSelected()) {
                adicionales.add(AdicionalEntrada.SILLA_VIBRATORIA);
            }
            List<LineaConfiteria> confiteria = new ArrayList<>(lstCarrito.getItems());
            Compra compra = fachada.prepararCompra(funcionSeleccionada, asientosSeleccionados,
                    chkEntradasPuntos.isSelected(), confiteria, cmbPromocion.getValue(), adicionales);
            if (confirmar("Confirmar compra", resumenCompra(compra))) {
                fachada.pagarCompra(compra);
                mostrarInfo("Compra " + compra.getCodigo() + " realizada.\nValor descontado de la tarjeta: "
                        + Formatos.pesos(compra.getTotal()) + "\nPuntos ganados: " + compra.getPuntosGanados());
                limpiarCompra();
                refrescarTodo();
            }
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    private String resumenCompra(Compra compra) {
        String texto = "";
        for (Entrada entrada : compra.getEntradas()) {
            texto = texto + entrada.getDescripcion() + "   " + Formatos.pesos(entrada.getPrecio()) + "\n";
        }
        for (LineaConfiteria linea : compra.getLineasConfiteria()) {
            texto = texto + linea + "   " + Formatos.pesos(linea.getSubtotal()) + "\n";
        }
        texto = texto + "\nSubtotal entradas: " + Formatos.pesos(compra.getSubtotalEntradas());
        texto = texto + "\nSubtotal confitería: " + Formatos.pesos(compra.getSubtotalConfiteria());
        texto = texto + "\nDescuento: " + Formatos.pesos(compra.getDescuento());
        texto = texto + "\nTotal a pagar: " + Formatos.pesos(compra.getTotal());
        texto = texto + "\nPuntos a redimir: " + compra.getPuntosRedimidos();
        texto = texto + "\n\nSaldo actual de la tarjeta: " + Formatos.pesos(fachada.consultarTarjeta().getSaldo());
        return texto;
    }

    @FXML
    private void solicitarRecarga() {
        try {
            long valor = leerValor(txtValorRecarga.getText());
            fachada.solicitarRecarga(valor);
            txtValorRecarga.clear();
            mostrarInfo("Solicitud enviada. Un administrador realizará la recarga de " + Formatos.pesos(valor) + ".");
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void verDetalleCompra() {
        lstDetalleCompra.getItems().clear();
        Compra compra = lstCompras.getSelectionModel().getSelectedItem();
        if (compra == null) {
            return;
        }
        for (Entrada entrada : compra.getEntradas()) {
            lstDetalleCompra.getItems().add(entrada.getCodigo() + " - " + entrada.getDescripcion() + " - "
                    + Formatos.pesos(entrada.getPrecio()));
        }
        for (LineaConfiteria linea : compra.getLineasConfiteria()) {
            lstDetalleCompra.getItems().add(linea.toString());
        }
        lstDetalleCompra.getItems().add("Total pagado: " + Formatos.pesos(compra.getTotal()));
        if (compra.getReembolso() != null) {
            lstDetalleCompra.getItems().add("Reembolso: " + compra.getReembolso().getPorcentaje() + " % - "
                    + Formatos.pesos(compra.getReembolso().getValor()));
        }
    }

    @FXML
    private void cancelarCompra() {
        Compra compra = lstCompras.getSelectionModel().getSelectedItem();
        if (compra == null) {
            mostrarError("Seleccione la compra que desea cancelar");
            return;
        }
        if (!confirmar("Cancelar compra " + compra.getCodigo(),
                "El reembolso depende del tiempo que falte para la función. ¿Desea continuar?")) {
            return;
        }
        try {
            Reembolso reembolso = fachada.cancelarCompra(compra);
            mostrarInfo("Compra cancelada. Se devolvió el " + reembolso.getPorcentaje() + " %: "
                    + Formatos.pesos(reembolso.getValor()) + " a su tarjeta.");
            refrescarTodo();
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void cerrarSesion() {
        navegador.irALogin();
    }

    private void refrescarTodo() {
        lstFunciones.getItems().setAll(fachada.consultarCartelera());
        if (funcionSeleccionada != null) {
            lstFunciones.getSelectionModel().select(funcionSeleccionada);
        }
        dibujarAsientos();
        cmbPromocion.getItems().setAll(fachada.consultarPromociones());

        TarjetaVirtual tarjeta = fachada.consultarTarjeta();
        lblNumeroTarjeta.setText(tarjeta.getNumero());
        if (tarjeta.isActiva()) {
            lblEstadoTarjeta.setText("Activa");
        } else {
            lblEstadoTarjeta.setText("Bloqueada");
        }
        lblSaldoTarjeta.setText(Formatos.pesos(tarjeta.getSaldo()));
        lstMovimientos.getItems().clear();
        for (Movimiento movimiento : fachada.consultarMovimientos()) {
            lstMovimientos.getItems().add(movimiento.getFecha().format(Formatos.FECHA_HORA) + "   "
                    + movimiento.getTipo() + "   " + Formatos.pesos(movimiento.getValor())
                    + "   saldo: " + Formatos.pesos(movimiento.getSaldoResultante()) + "   " + movimiento.getReferencia());
        }

        lstCompras.getItems().setAll(fachada.consultarMisCompras());
        lstDetalleCompra.getItems().clear();

        int puntos = fachada.consultarPuntosDisponibles();
        lblPuntosDisponibles.setText(puntos + " puntos vigentes");
        lstPuntos.getItems().clear();
        for (AcumulacionPuntos lote : fachada.consultarLotesDePuntos()) {
            String estado = "vigente";
            if (lote.estaVencida(fachada.fechaActual())) {
                estado = "vencido";
            }
            lstPuntos.getItems().add(lote.getReferencia() + "   obtenidos: " + lote.getPuntosObtenidos()
                    + "   restantes: " + lote.getPuntosRestantes() + "   vence: "
                    + lote.getFechaVencimiento().format(Formatos.FECHA) + "   (" + estado + ")");
        }

        lblSaldoCabecera.setText("Saldo: " + Formatos.pesos(tarjeta.getSaldo()));
        lblPuntosCabecera.setText("Puntos: " + puntos);
    }
}
