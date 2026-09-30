package co.edu.uniquindio.cinemauq.modelo.cartelera;

import co.edu.uniquindio.cinemauq.excepciones.AsientoNoDisponibleException;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoAsiento;

public class AsientoFuncion implements Cloneable {

    private final Asiento asiento;
    private EstadoAsiento estado;

    public AsientoFuncion(Asiento asiento) {
        this(asiento, EstadoAsiento.DISPONIBLE);
    }

    private AsientoFuncion(Asiento asiento, EstadoAsiento estado) {
        this.asiento = asiento;
        this.estado = estado;
    }

    public boolean estaDisponible() {
        return estado == EstadoAsiento.DISPONIBLE;
    }

    public void validarDisponible() {
        if (!estaDisponible()) {
            throw new AsientoNoDisponibleException("El asiento " + asiento.getCodigo() + " está " + estado.name().toLowerCase());
        }
    }

    public void vender() {
        validarDisponible();
        estado = EstadoAsiento.VENDIDO;
    }

    public void bloquear() {
        validarDisponible();
        estado = EstadoAsiento.BLOQUEADO;
    }

    public void liberar() {
        estado = EstadoAsiento.DISPONIBLE;
    }

    @Override
    public AsientoFuncion clone() {
        EstadoAsiento estadoCopia = EstadoAsiento.DISPONIBLE;
        if (estado == EstadoAsiento.BLOQUEADO) {
            estadoCopia = EstadoAsiento.BLOQUEADO;
        }
        return new AsientoFuncion(asiento, estadoCopia);
    }

    public Asiento getAsiento() {
        return asiento;
    }

    public EstadoAsiento getEstado() {
        return estado;
    }
}