package co.edu.uniquindio.cinemauq.modelo.puntos;

import java.time.LocalDate;

public class AcumulacionPuntos {

    public static final int MESES_VIGENCIA = 12;

    private final int puntosObtenidos;
    private int puntosRestantes;
    private final LocalDate fechaObtencion;
    private final LocalDate fechaVencimiento;
    private final String referencia;

    public AcumulacionPuntos(int puntos, LocalDate fechaObtencion, String referencia) {
        this.puntosObtenidos = puntos;
        this.puntosRestantes = puntos;
        this.fechaObtencion = fechaObtencion;
        this.fechaVencimiento = fechaObtencion.plusMonths(MESES_VIGENCIA);
        this.referencia = referencia;
    }

    public boolean estaVencida(LocalDate hoy) {
        return !hoy.isBefore(fechaVencimiento);
    }

    public int disponibles(LocalDate hoy) {
        if (estaVencida(hoy)) {
            return 0;
        }
        return puntosRestantes;
    }

    int consumir(int maximo) {
        int usados = Math.min(maximo, puntosRestantes);
        puntosRestantes -= usados;
        return usados;
    }

    int anular() {
        int anulados = puntosRestantes;
        puntosRestantes = 0;
        return anulados;
    }

    public int getPuntosObtenidos() {
        return puntosObtenidos;
    }

    public int getPuntosRestantes() {
        return puntosRestantes;
    }

    public LocalDate getFechaObtencion() {
        return fechaObtencion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getReferencia() {
        return referencia;
    }
}
