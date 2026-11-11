package co.edu.uniquindio.cinemauq.controlador.compra;

import javafx.scene.control.Button;

public class BotonPagar extends ComponenteCompra {

    private final Button boton;

    public BotonPagar(Button boton) {
        this.boton = boton;
    }

    public void setHabilitado(boolean habilitado) {
        boton.setDisable(!habilitado);
    }

    public boolean isHabilitado() {
        return !boton.isDisable();
    }
}
