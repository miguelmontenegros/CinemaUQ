package co.edu.uniquindio.cinemauq.controlador.compra;

import co.edu.uniquindio.cinemauq.modelo.compra.Promocion;
import javafx.scene.control.ComboBox;

import java.util.List;

public class SelectorPromocion extends ComponenteCompra {

    private final ComboBox<Promocion> combo;

    public SelectorPromocion(ComboBox<Promocion> combo) {
        this.combo = combo;
        combo.setOnAction(evento -> avisar(MediadorCompra.PROMOCION_CAMBIO));
    }

    public void cargar(List<Promocion> promociones) {
        Promocion actual = combo.getValue();
        combo.getItems().setAll(promociones);
        if (actual != null && promociones.contains(actual)) {
            combo.setValue(actual);
        }
    }

    public Promocion getPromocion() {
        return combo.getValue();
    }

    public void limpiar() {
        combo.getSelectionModel().clearSelection();
        combo.setValue(null);
    }
}
