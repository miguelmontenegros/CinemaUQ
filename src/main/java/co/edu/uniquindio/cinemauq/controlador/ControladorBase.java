package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.fachada.CinemaUQFachada;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public abstract class ControladorBase {

    protected CinemaUQFachada fachada;
    protected Navegador navegador;

    public void inicializar(CinemaUQFachada fachada, Navegador navegador) {
        this.fachada = fachada;
        this.navegador = navegador;
        alCargar();
    }

    protected abstract void alCargar();

    protected void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("CinemaUQ");
        alerta.setHeaderText("No se pudo completar la operación");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    protected void mostrarInfo(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("CinemaUQ");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    protected boolean confirmar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("CinemaUQ");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

    protected long leerValor(String texto) {
        String limpio = texto.replace(".", "").replace(",", "").replace("$", "").trim();
        try {
            return Long.parseLong(limpio);
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("Ingrese un valor numérico válido");
        }
    }
}
