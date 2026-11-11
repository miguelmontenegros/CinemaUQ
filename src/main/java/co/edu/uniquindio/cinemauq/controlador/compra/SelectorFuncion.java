package co.edu.uniquindio.cinemauq.controlador.compra;

import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import javafx.scene.control.ListView;

import java.util.List;

public class SelectorFuncion extends ComponenteCompra {

    private final ListView<Funcion> lista;
    private Funcion funcion;

    public SelectorFuncion(ListView<Funcion> lista) {
        this.lista = lista;
        lista.setOnMouseClicked(evento -> elegir(lista.getSelectionModel().getSelectedItem()));
    }

    public void elegir(Funcion nueva) {
        if (nueva != null && nueva != funcion) {
            funcion = nueva;
            avisar(MediadorCompra.FUNCION_ELEGIDA);
        }
    }

    public void cargar(List<Funcion> funciones) {
        lista.getItems().setAll(funciones);
        if (funcion != null && funciones.contains(funcion)) {
            lista.getSelectionModel().select(funcion);
        } else if (funcion != null) {
            funcion = null;
            avisar(MediadorCompra.FUNCION_ELEGIDA);
        }
    }

    public Funcion getFuncion() {
        return funcion;
    }
}
