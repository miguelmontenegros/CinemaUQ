package co.edu.uniquindio.cinemauq.servicio.politicas;



import co.edu.uniquindio.cinemauq.excepciones.CancelacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

import java.time.Duration;
import java.time.LocalDateTime;


public class ReembolsoPorAnticipacion implements PoliticaReembolso {

    @Override
    public int calcularPorcentaje(Compra compra, LocalDateTime momento) {
        Duration faltante = Duration.between(momento, compra.getFuncion().getFechaHora());
        if (faltante.compareTo(Duration.ofHours(24)) > 0) {
            return 100;
        }
        if (faltante.compareTo(Duration.ofHours(2)) >= 0) {
            return 80;
        }
        throw new CancelacionNoPermitidaException("No se puede cancelar: faltan menos de 2 horas para la función");
    }

    @Override
    public String getMotivo() {
        return "Cancelación solicitada por el cliente";
    }

    @Override
    public String getDescripcion() {
        return "Cancelación del cliente: 100 % con más de 24 horas de anticipación, 80 % entre 2 y 24 horas, "
                + "no se permite con menos de 2 horas. Los puntos ganados en la compra se anulan.";
    }
}