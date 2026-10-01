package co.edu.uniquindio.cinemauq.modelo.compra.emision;

import co.edu.uniquindio.cinemauq.modelo.enums.TipoAsiento;

import java.util.EnumMap;
import java.util.Map;

public final class CatalogoEmisores {

    private static final Map<TipoAsiento, EmisorEntradas> PAGADAS = new EnumMap<>(TipoAsiento.class);
    private static final EmisorEntradas CORTESIA = new EmisorEntradaCortesia();

    static {
        PAGADAS.put(TipoAsiento.GENERAL, new EmisorEntradaGeneral());
        PAGADAS.put(TipoAsiento.PREFERENCIAL, new EmisorEntradaPreferencial());
    }

    private CatalogoEmisores() {
    }

    public static EmisorEntradas paraAsiento(TipoAsiento tipo) {
        return PAGADAS.get(tipo);
    }

    public static EmisorEntradas cortesia() {
        return CORTESIA;
    }
}
