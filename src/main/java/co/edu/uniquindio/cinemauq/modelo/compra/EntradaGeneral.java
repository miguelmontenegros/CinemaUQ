package co.edu.uniquindio.cinemauq.modelo.compra;

import co.edu.uniquindio.cinemauq.modelo.cartelera.Asiento;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;

public class EntradaGeneral extends Entrada {

    public EntradaGeneral(String codigo, Funcion funcion, Asiento asiento) {
        super(codigo, funcion, asiento);
    }

    @Override
    public long getPrecio() {
        return getFuncion().getPrecioBase();
    }

    @Override
    public String getTipo() {
        return "General";
    }
}
