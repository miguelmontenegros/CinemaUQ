package co.edu.uniquindio.cinemauq.modelo.enums;

public enum TipoAsiento {
    GENERAL(120), PREFERENCIAL(170);

    private final int puntosRedencion;

    TipoAsiento(int puntosRedencion) {
        this.puntosRedencion = puntosRedencion;
    }

    public int getPuntosRedencion() {
        return puntosRedencion;
    }
}
