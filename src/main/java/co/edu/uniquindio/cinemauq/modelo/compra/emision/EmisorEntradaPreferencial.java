package co.edu.uniquindio.cinemauq.modelo.compra.emision;

import co.edu.uniquindio.cinemauq.modelo.cartelera.Asiento;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Entrada;
import co.edu.uniquindio.cinemauq.modelo.compra.EntradaPreferencial;

public class EmisorEntradaPreferencial extends EmisorEntradas {

    @Override
    protected Entrada crearEntrada(String codigo, Funcion funcion, Asiento asiento) {
        return new EntradaPreferencial(codigo, funcion, asiento);
    }
}
