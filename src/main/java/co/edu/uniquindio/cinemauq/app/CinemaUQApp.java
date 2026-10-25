package co.edu.uniquindio.cinemauq.app;

import co.edu.uniquindio.cinemauq.controlador.Navegador;
import co.edu.uniquindio.cinemauq.datos.DatosIniciales;
import co.edu.uniquindio.cinemauq.fachada.CinemaUQFachada;
import co.edu.uniquindio.cinemauq.servicio.RelojSistema;
import javafx.application.Application;
import javafx.stage.Stage;

public class CinemaUQApp extends Application {

    @Override
    public void start(Stage ventana) {
        ContextoAplicacion contexto = new ContextoAplicacion(new RelojSistema());
        DatosIniciales.cargar(contexto);
        CinemaUQFachada fachada = new CinemaUQFachada(contexto);
        Navegador navegador = new Navegador(ventana, fachada);
        navegador.irALogin();
        ventana.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}