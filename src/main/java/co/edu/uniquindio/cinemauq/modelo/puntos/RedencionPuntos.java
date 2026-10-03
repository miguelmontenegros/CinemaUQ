package co.edu.uniquindio.cinemauq.modelo.puntos;

import java.time.LocalDate;

public class RedencionPuntos {

    private final int puntos;
    private final LocalDate fecha;
    private final String referencia;

    public RedencionPuntos(int puntos, LocalDate fecha, String referencia) {
        this.puntos = puntos;
        this.fecha = fecha;
        this.referencia = referencia;
    }

    public int getPuntos() {
        return puntos;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getReferencia() {
        return referencia;
    }
}
