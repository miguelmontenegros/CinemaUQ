package co.edu.uniquindio.cinemauq.modelo.compra;

import co.edu.uniquindio.cinemauq.modelo.cartelera.Asiento;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;

public class EntradaCortesia extends Entrada {

    public EntradaCortesia(String codigo, Funcion funcion, Asiento asiento) {
        super(codigo, funcion, asiento);
    }

    @Override
    public long getPrecio() {
        return 0;
    }

    @Override
    public boolean isCortesia() {
        return true;
    }

    @Override
    public String getTipo() {
        return "Cortesía (puntos)";
    }
}
