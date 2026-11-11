package co.edu.uniquindio.cinemauq.controlador.compra;

import co.edu.uniquindio.cinemauq.modelo.cartelera.AsientoFuncion;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoAsiento;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoAsiento;
import co.edu.uniquindio.cinemauq.util.Formatos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapaAsientos extends ComponenteCompra {

    private final GridPane grilla;
    private final Label lblFuncion;
    private final Label lblSeleccion;
    private final List<String> seleccionados = new ArrayList<>();
    private Funcion funcion;

    public MapaAsientos(GridPane grilla, Label lblFuncion, Label lblSeleccion) {
        this.grilla = grilla;
        this.lblFuncion = lblFuncion;
        this.lblSeleccion = lblSeleccion;
    }

    public void mostrar(Funcion nueva) {
        funcion = nueva;
        seleccionados.clear();
        dibujar();
    }

    public void limpiarSeleccion() {
        seleccionados.clear();
        dibujar();
    }

    public void alternar(String codigo) {
        if (seleccionados.contains(codigo)) {
            seleccionados.remove(codigo);
        } else {
            seleccionados.add(codigo);
        }
        dibujar();
        avisar(MediadorCompra.ASIENTOS_CAMBIARON);
    }

    public List<String> getSeleccionados() {
        return Collections.unmodifiableList(seleccionados);
    }

    public int puntosParaRedimir() {
        int puntos = 0;
        for (String codigo : seleccionados) {
            puntos = puntos + funcion.getAsiento(codigo).getAsiento().getTipo().getPuntosRedencion();
        }
        return puntos;
    }

    public int contarPreferenciales() {
        int preferenciales = 0;
        for (String codigo : seleccionados) {
            if (funcion.getAsiento(codigo).getAsiento().getTipo() == TipoAsiento.PREFERENCIAL) {
                preferenciales++;
            }
        }
        return preferenciales;
    }

    private void dibujar() {
        grilla.getChildren().clear();
        if (funcion == null) {
            lblFuncion.setText("Seleccione una función de la cartelera");
            lblSeleccion.setText("");
            return;
        }
        lblFuncion.setText(funcion.getPelicula().getTitulo() + " - " + funcion.getSala()
                + " - " + funcion.getFechaHora().format(Formatos.FECHA_HORA)
                + "   |   General " + Formatos.pesos(funcion.getPrecioBase()) + ", preferencial +35 %");
        for (AsientoFuncion asientoFuncion : funcion.getAsientos()) {
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
            } else if (seleccionados.contains(codigo)) {
                boton.getStyleClass().add("asiento-seleccionado");
            }
            boton.setOnAction(evento -> alternar(codigo));
            int columna = asientoFuncion.getAsiento().getNumero() - 1;
            int fila = asientoFuncion.getAsiento().getFila() - 'A';
            grilla.add(boton, columna, fila);
        }
        if (seleccionados.isEmpty()) {
            lblSeleccion.setText("No hay asientos seleccionados");
        } else {
            lblSeleccion.setText("Asientos seleccionados: " + String.join(", ", seleccionados));
        }
    }
}
