package co.edu.uniquindio.cinemauq.servicio.politicas;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

import java.time.LocalDateTime;

public class ReembolsoFuncionCancelada implements PoliticaReembolso {

    @Override
    public int calcularPorcentaje(Compra compra, LocalDateTime momento) {
        return 100;
    }

    @Override
    public String getMotivo() {
        return "Función cancelada por el cine";
    }

    @Override
    public boolean devuelvePuntosRedimidos() {
        return true;
    }
}