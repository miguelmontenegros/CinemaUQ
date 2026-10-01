package co.edu.uniquindio.cinemauq.servicio.politicas;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

public interface ReglaAcumulacionPuntos {

    int calcularPuntos(Compra compra);

    String getDescripcion();
}