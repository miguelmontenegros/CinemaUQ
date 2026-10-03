package co.edu.uniquindio.cinemauq.modelo.tarjeta;

import co.edu.uniquindio.cinemauq.modelo.enums.TipoMovimiento;

import java.time.LocalDateTime;

public class Movimiento {

    private final TipoMovimiento tipo;
    private final long valor;
    private final long saldoResultante;
    private final LocalDateTime fecha;
    private final String referencia;

    public Movimiento(TipoMovimiento tipo, long valor, long saldoResultante, LocalDateTime fecha, String referencia) {
        this.tipo = tipo;
        this.valor = valor;
        this.saldoResultante = saldoResultante;
        this.fecha = fecha;
        this.referencia = referencia;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public long getValor() {
        return valor;
    }

    public long getSaldoResultante() {
        return saldoResultante;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getReferencia() {
        return referencia;
    }
}
