package co.edu.uniquindio.cinemauq.modelo.compra.emision;

import co.edu.uniquindio.cinemauq.modelo.GeneradorCodigos;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Asiento;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Entrada;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoCodigo;

public abstract class EmisorEntradas {

    public final Entrada emitir(Funcion funcion, Asiento asiento) {
        funcion.getAsiento(asiento.getCodigo()).validarDisponible();
        String codigo = GeneradorCodigos.getInstancia().siguiente(TipoCodigo.ENTRADA);
        return crearEntrada(codigo, funcion, asiento);
    }

    protected abstract Entrada crearEntrada(String codigo, Funcion funcion, Asiento asiento);
}
