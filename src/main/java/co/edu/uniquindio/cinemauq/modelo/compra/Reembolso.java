package co.edu.uniquindio.cinemauq.modelo.compra;

import co.edu.uniquindio.cinemauq.modelo.GeneradorCodigos;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoCodigo;

import java.time.LocalDateTime;

public class Reembolso {

    private final String codigo;
    private final int porcentaje;
    private final long valor;
    private final LocalDateTime fecha;
    private final String motivo;

    public Reembolso(int porcentaje, long valor, LocalDateTime fecha, String motivo) {
        this.codigo = GeneradorCodigos.getInstancia().siguiente(TipoCodigo.REEMBOLSO);
        this.porcentaje = porcentaje;
        this.valor = valor;
        this.fecha = fecha;
        this.motivo = motivo;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public long getValor() {
        return valor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getMotivo() {
        return motivo;
    }
}
