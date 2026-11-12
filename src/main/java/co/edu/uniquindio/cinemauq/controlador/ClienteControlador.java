package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.controlador.compra.BotonPagar;
import co.edu.uniquindio.cinemauq.controlador.compra.CarritoConfiteria;
import co.edu.uniquindio.cinemauq.controlador.compra.MapaAsientos;
import co.edu.uniquindio.cinemauq.controlador.compra.OpcionesEntrada;
import co.edu.uniquindio.cinemauq.controlador.compra.PantallaCompraMediador;
import co.edu.uniquindio.cinemauq.controlador.compra.ResumenCompra;
import co.edu.uniquindio.cinemauq.controlador.compra.SelectorFuncion;
import co.edu.uniquindio.cinemauq.controlador.compra.SelectorPromocion;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.Entrada;
import co.edu.uniquindio.cinemauq.modelo.compra.LineaConfiteria;
import co.edu.uniquindio.cinemauq.modelo.compra.Promocion;
import co.edu.uniquindio.cinemauq.modelo.compra.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.puntos.AcumulacionPuntos;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.Movimiento;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.util.Formatos;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

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
    private Label lblResumenCompra;
    @FXML
    private Button btnPagar;

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
    private Label lblPuntosPorVencer;
    @FXML
    private ListView<String> lstPuntos;
    @FXML
    private ListView<String> lstNotificaciones;

    private CarritoConfiteria carrito;
    private PantallaCompraMediador pantallaCompra;

    @Override
    protected void alCargar() {
        lblBienvenida.setText("Hola, " + fachada.getUsuarioActual().getNombre());
        carrito = new CarritoConfiteria(cmbProductos, txtCantidad, chkProductoPuntos, lstCarrito);
        pantallaCompra = new PantallaCompraMediador(fachada,
                new SelectorFuncion(lstFunciones),
                new MapaAsientos(gridAsientos, lblFuncion, lblSeleccion),
                new OpcionesEntrada(chkEntradasPuntos, chkGafas3D, chkSillaVibratoria),
                carrito,
                new SelectorPromocion(cmbPromocion),
                new ResumenCompra(lblResumenCompra),
                new BotonPagar(btnPagar));
        refrescarTodo();
    }

    @FXML
    private void agregarProducto() {
        try {
            carrito.agregar();
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void quitarProducto() {
        carrito.quitarSeleccionado();
    }

    @FXML
    private void limpiarCompra() {
        pantallaCompra.reiniciar();
    }

    @FXML
    private void comprar() {
        try {
            Compra compra = pantallaCompra.armarCompra();
            if (confirmar("Confirmar compra", resumenCompra(compra))) {
                fachada.pagarCompra(compra);
                mostrarInfo("Compra " + compra.getCodigo() + " realizada.\nValor descontado de la tarjeta: "
                        + Formatos.pesos(compra.getTotal()) + "\nPuntos ganados: " + compra.getPuntosGanados());
                pantallaCompra.reiniciar();
                refrescarTodo();
            }
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    private String resumenCompra(Compra compra) {
        StringBuilder texto = new StringBuilder();
        for (Entrada entrada : compra.getEntradas()) {
            texto.append(entrada.getDescripcion()).append("   ").append(Formatos.pesos(entrada.getPrecio())).append("\n");
        }
        for (LineaConfiteria linea : compra.getLineasConfiteria()) {
            texto.append(linea).append("   ").append(Formatos.pesos(linea.getSubtotal())).append("\n");
        }
        texto.append("\nSubtotal entradas: ").append(Formatos.pesos(compra.getSubtotalEntradas()));
        texto.append("\nSubtotal confitería: ").append(Formatos.pesos(compra.getSubtotalConfiteria()));
        texto.append("\nDescuento: ").append(Formatos.pesos(compra.getDescuento()));
        texto.append("\nTotal a pagar: ").append(Formatos.pesos(compra.getTotal()));
        texto.append("\nPuntos a redimir: ").append(compra.getPuntosRedimidos());
        texto.append("\n\nSaldo actual de la tarjeta: ").append(Formatos.pesos(fachada.consultarTarjeta().getSaldo()));
        return texto.toString();
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
                    + Formatos.pesos(compra.getReembolso().getValor()) + " - " + compra.getReembolso().getMotivo());
        }
    }

    @FXML
    private void cancelarCompra() {
        Compra compra = lstCompras.getSelectionModel().getSelectedItem();
        if (compra == null) {
            mostrarError("Seleccione la compra que desea cancelar");
            return;
        }
        if (!confirmar("Cancelar compra " + compra.getCodigo(), fachada.describirPoliticaCancelacion()
                + "\n\n¿Desea continuar?")) {
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
        pantallaCompra.refrescarDatos();

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
        int porVencer = fachada.consultarPuntosPorVencer();
        if (porVencer > 0) {
            lblPuntosPorVencer.setText(porVencer + " puntos vencen en los próximos "
                    + fachada.diasAlertaVencimiento() + " días. Úselos en entradas o confitería.");
        } else {
            lblPuntosPorVencer.setText("No tiene puntos por vencer en los próximos "
                    + fachada.diasAlertaVencimiento() + " días.");
        }
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

        lstNotificaciones.getItems().setAll(fachada.consultarNotificaciones());

        lblSaldoCabecera.setText("Saldo: " + Formatos.pesos(tarjeta.getSaldo()));
        lblPuntosCabecera.setText("Puntos: " + puntos);
    }
}
