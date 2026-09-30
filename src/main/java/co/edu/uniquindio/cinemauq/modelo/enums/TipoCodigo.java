package co.edu.uniquindio.cinemauq.modelo.enums;

public enum TipoCodigo {
    COMPRA("CMP"), ENTRADA("ENT"), FUNCION("FUN"), SOLICITUD_RECARGA("SOL"), REEMBOLSO("REM");

    private final String prefijo;

    TipoCodigo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getPrefijo() {
        return prefijo;
    }
}
