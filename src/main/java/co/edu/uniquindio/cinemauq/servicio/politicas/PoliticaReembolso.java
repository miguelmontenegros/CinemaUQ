package co.edu.uniquindio.cinemauq.servicio.politicas;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

import java.time.LocalDateTime;


public interface PoliticaReembolso {

    /** @throws co.edu.uniquindio.cinemauq.excepciones.CancelacionNoPermitidaException si no aplica reembolso */
    int calcularPorcentaje(Compra compra, LocalDateTime momento);

    String getMotivo();

    default boolean devuelvePuntosRedimidos() {
        return false;
    }
}