package co.edu.uniquindio.cinemauq.modelo.cartelera;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoAsiento;

public class Asiento {

    private final char fila;
    private final int numero;
    private final TipoAsiento tipo;

    public Asiento(char fila, int numero, TipoAsiento tipo) {
        this.fila = fila;
        this.numero = numero;
        this.tipo = tipo;
    }

    public String getCodigo() {
        return String.valueOf(fila) + numero;
    }

    public char getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    public TipoAsiento getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return getCodigo();
    }
}