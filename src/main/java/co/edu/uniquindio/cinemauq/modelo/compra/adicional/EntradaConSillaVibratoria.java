package co.edu.uniquindio.cinemauq.modelo.compra.adicional;

import co.edu.uniquindio.cinemauq.modelo.compra.Entrada;
import co.edu.uniquindio.cinemauq.modelo.enums.AdicionalEntrada;

public class EntradaConSillaVibratoria extends DecoradorEntrada {

    public EntradaConSillaVibratoria(Entrada entrada) {
        super(entrada, AdicionalEntrada.SILLA_VIBRATORIA);
    }
}