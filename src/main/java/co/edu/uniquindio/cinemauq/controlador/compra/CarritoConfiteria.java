package co.edu.uniquindio.cinemauq.controlador.compra;

import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.modelo.compra.LineaConfiteria;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ItemConfiteria;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class CarritoConfiteria extends ComponenteCompra {

    private final ComboBox<ItemConfiteria> cmbProductos;
    private final TextField txtCantidad;
    private final CheckBox chkConPuntos;
    private final ListView<LineaConfiteria> lstLineas;

    public CarritoConfiteria(ComboBox<ItemConfiteria> cmbProductos, TextField txtCantidad, CheckBox chkConPuntos,
                             ListView<LineaConfiteria> lstLineas) {
        this.cmbProductos = cmbProductos;
        this.txtCantidad = txtCantidad;
        this.chkConPuntos = chkConPuntos;
        this.lstLineas = lstLineas;
        txtCantidad.setText("1");
    }

    public void cargarProductos(List<ItemConfiteria> productos) {
        cmbProductos.getItems().setAll(productos);
    }

    public void agregar() {
        ItemConfiteria item = cmbProductos.getValue();
        if (item == null) {
            throw new DatoInvalidoException("Seleccione un producto o combo");
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("Ingrese una cantidad numérica");
        }
        if (cantidad <= 0) {
            throw new DatoInvalidoException("La cantidad debe ser mayor que cero");
        }
        lstLineas.getItems().add(new LineaConfiteria(item, cantidad, chkConPuntos.isSelected()));
        avisar(MediadorCompra.CARRITO_CAMBIO);
    }

    public void quitarSeleccionado() {
        LineaConfiteria linea = lstLineas.getSelectionModel().getSelectedItem();
        if (linea != null) {
            lstLineas.getItems().remove(linea);
            avisar(MediadorCompra.CARRITO_CAMBIO);
        }
    }

    public void vaciar() {
        lstLineas.getItems().clear();
        chkConPuntos.setSelected(false);
        txtCantidad.setText("1");
    }

    public List<LineaConfiteria> getLineas() {
        return new ArrayList<>(lstLineas.getItems());
    }

    public boolean estaVacio() {
        return lstLineas.getItems().isEmpty();
    }

    public int contarUnidades() {
        int unidades = 0;
        for (LineaConfiteria linea : lstLineas.getItems()) {
            unidades = unidades + linea.getCantidad();
        }
        return unidades;
    }

    public int puntosParaRedimir() {
        int puntos = 0;
        for (LineaConfiteria linea : lstLineas.getItems()) {
            puntos = puntos + linea.getPuntos();
        }
        return puntos;
    }

    public long subtotalEnDinero() {
        long subtotal = 0;
        for (LineaConfiteria linea : lstLineas.getItems()) {
            subtotal = subtotal + linea.getSubtotal();
        }
        return subtotal;
    }
}
