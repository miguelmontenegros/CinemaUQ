package co.edu.uniquindio.cinemauq.modelo.compra;

import co.edu.uniquindio.cinemauq.modelo.cartelera.Asiento;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.contratos.Redimible;
import co.edu.uniquindio.cinemauq.modelo.contratos.Vendible;

public abstract class Entrada implements Vendible, Redimible {

    private final String codigo;
    private final Funcion funcion;
    private final Asiento asiento;

    protected Entrada(String codigo, Funcion funcion, Asiento asiento) {
        this.codigo = codigo;
        this.funcion = funcion;
        this.asiento = asiento;
    }

    public abstract String getTipo();

    public boolean isCortesia() {
        return false;
    }

    @Override
    public int getCostoPuntos() {
        return asiento.getTipo().getPuntosRedencion();
    }

    @Override
    public String getDescripcion() {
        return "Entrada " + getTipo() + " " + asiento.getCodigo() + " · " + funcion.getPelicula().getTitulo();
    }

    public String getCodigo() {
        return codigo;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public Asiento getAsiento() {
        return asiento;
    }

    @Override
    public String toString() {
        return getDescripcion();
    }
}
