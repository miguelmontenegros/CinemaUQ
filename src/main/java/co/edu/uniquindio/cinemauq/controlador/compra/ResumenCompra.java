package co.edu.uniquindio.cinemauq.controlador.compra;

import javafx.scene.control.Label;

import java.util.List;

public class ResumenCompra extends ComponenteCompra {

    private final Label lblResumen;

    public ResumenCompra(Label lblResumen) {
        this.lblResumen = lblResumen;
    }

    public void mostrar(List<String> lineas, String aviso) {
        String texto = String.join("\n", lineas);
        if (aviso != null && !aviso.isEmpty()) {
            texto = texto + "\n\n" + aviso;
        }
        lblResumen.setText(texto);
    }
}
