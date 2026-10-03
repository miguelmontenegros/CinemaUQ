package co.edu.uniquindio.cinemauq.servicio.politicas;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;


public class AcumulacionPorValorPagado implements ReglaAcumulacionPuntos {

    public static final long PESOS_POR_PUNTO = 1_000;

    @Override
    public int calcularPuntos(Compra compra) {
        return (int) (compra.getTotal() / PESOS_POR_PUNTO);
    }

    @Override
    public String getDescripcion() {
        return "1 punto por cada $1.000 pagados";
    }
}