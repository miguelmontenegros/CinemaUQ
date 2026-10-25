package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.fachada.CinemaUQFachada;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Navegador {

    private static final String RUTA_VISTAS = "/co/edu/uniquindio/cinemauq/vista/";

    private final Stage ventana;
    private final CinemaUQFachada fachada;

    public Navegador(Stage ventana, CinemaUQFachada fachada) {
        this.ventana = ventana;
        this.fachada = fachada;
    }

    public void irALogin() {
        fachada.cerrarSesion();
        mostrar("login-view.fxml", "CinemaUQ - Iniciar sesión", 520, 640);
    }

    public void irACliente() {
        mostrar("cliente-view.fxml", "CinemaUQ - Cliente", 1280, 780);
    }

    public void irAAdministrador() {
        mostrar("admin-view.fxml", "CinemaUQ - Administrador", 1100, 720);
    }

    private void mostrar(String archivo, String titulo, double ancho, double alto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_VISTAS + archivo));
            Parent raiz = loader.load();
            ControladorBase controlador = loader.getController();
            controlador.inicializar(fachada, this);
            Scene escena = new Scene(raiz, ancho, alto);
            escena.getStylesheets().add(getClass().getResource(RUTA_VISTAS + "estilos.css").toExternalForm());
            ventana.setTitle(titulo);
            ventana.setScene(escena);
            ventana.centerOnScreen();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar la vista " + archivo, e);
        }
    }
}
