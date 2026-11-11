package co.edu.uniquindio.cinemauq.servicio.politicas;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

import java.time.LocalDateTime;


public interface PoliticaReembolso {

    int calcularPorcentaje(Compra compra, LocalDateTime momento);

    String getMotivo();

    default String getDescripcion() {
        return getMotivo();
    }

    default boolean devuelvePuntosRedimidos() {
        return false;
    }
}