package co.edu.uniquindio.cinemauq.servicio.eventos;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class EstadisticasOperacion implements ObservadorOperaciones {

    private int comprasConfirmadas;
    private int entradasVendidas;
    private long valorVendido;
    private int comprasCanceladas;
    private long valorReembolsado;
    private int funcionesCanceladas;
    private int recargas;
    private long valorRecargado;
    private final Map<LocalDate, Long> ventasPorDia = new TreeMap<>();
    private final Map<LocalDate, Long> reembolsosPorDia = new TreeMap<>();

    @Override
    public void alOcurrir(EventoOperacion evento) {
        LocalDate dia = evento.getMomento().toLocalDate();
        switch (evento.getTipo()) {
            case COMPRA_CONFIRMADA:
                comprasConfirmadas++;
                entradasVendidas = entradasVendidas + evento.getCompra().getEntradas().size();
                valorVendido = valorVendido + evento.getValor();
                sumar(ventasPorDia, dia, evento.getValor());
                break;
            case COMPRA_CANCELADA:
                comprasCanceladas++;
                valorReembolsado = valorReembolsado + evento.getValor();
                sumar(reembolsosPorDia, dia, evento.getValor());
                break;
            case FUNCION_CANCELADA:
                funcionesCanceladas++;
                break;
            case RECARGA_REALIZADA:
                recargas++;
                valorRecargado = valorRecargado + evento.getValor();
                break;
            default:
                break;
        }
    }

    private void sumar(Map<LocalDate, Long> mapa, LocalDate dia, long valor) {
        Long actual = mapa.get(dia);
        if (actual == null) {
            actual = 0L;
        }
        mapa.put(dia, actual + valor);
    }

    public int getComprasConfirmadas() {
        return comprasConfirmadas;
    }

    public int getEntradasVendidas() {
        return entradasVendidas;
    }

    public long getValorVendido() {
        return valorVendido;
    }

    public int getComprasCanceladas() {
        return comprasCanceladas;
    }

    public long getValorReembolsado() {
        return valorReembolsado;
    }

    public long getIngresoNeto() {
        return valorVendido - valorReembolsado;
    }

    public int getFuncionesCanceladas() {
        return funcionesCanceladas;
    }

    public int getRecargas() {
        return recargas;
    }

    public long getValorRecargado() {
        return valorRecargado;
    }

    public long getTicketPromedio() {
        if (comprasConfirmadas == 0) {
            return 0;
        }
        return valorVendido / comprasConfirmadas;
    }

    public double getTasaCancelacion() {
        if (comprasConfirmadas == 0) {
            return 0;
        }
        return 100.0 * comprasCanceladas / comprasConfirmadas;
    }

    public Map<LocalDate, Long> getVentasPorDia() {
        return new TreeMap<>(ventasPorDia);
    }

    public Map<LocalDate, Long> getReembolsosPorDia() {
        return new TreeMap<>(reembolsosPorDia);
    }
}