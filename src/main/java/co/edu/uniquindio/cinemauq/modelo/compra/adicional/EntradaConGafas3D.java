package co.edu.uniquindio.cinemauq.modelo.compra.adicional;

import co.edu.uniquindio.cinemauq.modelo.compra.Entrada;
import co.edu.uniquindio.cinemauq.modelo.enums.AdicionalEntrada;

public class EntradaConGafas3D extends DecoradorEntrada {

    public EntradaConGafas3D(Entrada entrada) {
        super(entrada, AdicionalEntrada.GAFAS_3D);
    }
}