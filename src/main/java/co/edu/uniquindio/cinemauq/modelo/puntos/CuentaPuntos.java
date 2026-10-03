package co.edu.uniquindio.cinemauq.modelo.puntos;

import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.excepciones.PuntosInsuficientesException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CuentaPuntos {

    private final List<AcumulacionPuntos> acumulaciones = new ArrayList<>();
    private final List<RedencionPuntos> redenciones = new ArrayList<>();

    public void acumular(int puntos, LocalDate fecha, String referencia) {
        if (puntos <= 0) {
            return;
        }
        acumulaciones.add(new AcumulacionPuntos(puntos, fecha, referencia));
    }

    public int getPuntosDisponibles(LocalDate hoy) {
        int total = 0;
        for (AcumulacionPuntos lote : acumulaciones) {
            total = total + lote.disponibles(hoy);
        }
        return total;
    }

    public void validarRedencion(int puntos, LocalDate hoy) {
        if (puntos < 0) {
            throw new DatoInvalidoException("La cantidad de puntos no puede ser negativa");
        }
        int disponibles = getPuntosDisponibles(hoy);
        if (disponibles < puntos) {
            throw new PuntosInsuficientesException("Puntos insuficientes: disponibles " + disponibles
                    + ", requeridos " + puntos);
        }
    }

    public void redimir(int puntos, LocalDate hoy, String referencia) {
        if (puntos == 0) {
            return;
        }
        validarRedencion(puntos, hoy);

        List<AcumulacionPuntos> vigentes = lotesVigentesOrdenadosPorVencimiento(hoy);
        int pendientes = puntos;
        for (AcumulacionPuntos lote : vigentes) {
            if (pendientes == 0) {
                break;
            }
            int usados = lote.consumir(pendientes);
            pendientes = pendientes - usados;
        }
        redenciones.add(new RedencionPuntos(puntos, hoy, referencia));
    }

    private List<AcumulacionPuntos> lotesVigentesOrdenadosPorVencimiento(LocalDate hoy) {
        List<AcumulacionPuntos> ordenados = new ArrayList<>();
        for (AcumulacionPuntos lote : acumulaciones) {
            if (lote.disponibles(hoy) > 0) {
                int posicion = 0;
                while (posicion < ordenados.size()
                        && !lote.getFechaVencimiento().isBefore(ordenados.get(posicion).getFechaVencimiento())) {
                    posicion++;
                }
                ordenados.add(posicion, lote);
            }
        }
        return ordenados;
    }

    public int anularAcumulacion(String referencia) {
        int anulados = 0;
        for (AcumulacionPuntos lote : acumulaciones) {
            if (lote.getReferencia().equals(referencia)) {
                anulados = anulados + lote.anular();
            }
        }
        return anulados;
    }

    public List<AcumulacionPuntos> getAcumulaciones() {
        return Collections.unmodifiableList(acumulaciones);
    }

    public List<RedencionPuntos> getRedenciones() {
        return Collections.unmodifiableList(redenciones);
    }
}
