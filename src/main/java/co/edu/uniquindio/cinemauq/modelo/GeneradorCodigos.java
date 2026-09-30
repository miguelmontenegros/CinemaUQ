package co.edu.uniquindio.cinemauq.modelo;


import co.edu.uniquindio.cinemauq.modelo.enums.TipoCodigo;

import java.util.EnumMap;
import java.util.Map;


public final class GeneradorCodigos {

    private static final GeneradorCodigos INSTANCIA = new GeneradorCodigos();

    private final Map<TipoCodigo, Integer> consecutivos = new EnumMap<>(TipoCodigo.class);

    private GeneradorCodigos() {
    }

    public static GeneradorCodigos getInstancia() {
        return INSTANCIA;
    }

    public synchronized String siguiente(TipoCodigo tipo) {
        int numero = 1;
        if (consecutivos.containsKey(tipo)) {
            numero = consecutivos.get(tipo) + 1;
        }
        consecutivos.put(tipo, numero);
        return String.format("%s-%05d", tipo.getPrefijo(), numero);
    }
}