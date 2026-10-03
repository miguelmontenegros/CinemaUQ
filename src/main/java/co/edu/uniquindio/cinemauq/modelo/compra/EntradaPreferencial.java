package co.edu.uniquindio.cinemauq.modelo.compra;

import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Asiento;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoAsiento;

public class EntradaPreferencial extends Entrada {

    public static final double RECARGO = 0.35;

    public EntradaPreferencial(String codigo, Funcion funcion, Asiento asiento) {
        super(codigo, funcion, asiento);
        if (asiento.getTipo() != TipoAsiento.PREFERENCIAL) {
            throw new DatoInvalidoException("El asiento " + asiento.getCodigo() + " no es preferencial");
        }
    }

    @Override
    public long getPrecio() {
        return Math.round(getFuncion().getPrecioBase() * (1 + RECARGO));
    }

    @Override
    public String getTipo() {
        return "Preferencial";
    }
}
