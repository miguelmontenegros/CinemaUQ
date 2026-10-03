package co.edu.uniquindio.cinemauq.modelo.cartelera;

import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.excepciones.EntidadNoEncontradaException;
import co.edu.uniquindio.cinemauq.modelo.contratos.Identificable;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoAsiento;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoSala;
import co.edu.uniquindio.cinemauq.util.Validaciones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sala implements Identificable {

    public static final int MAX_FILAS = 26;

    private final int numero;
    private final String nombre;
    private final TipoSala tipo;
    private final int filas;
    private final int columnas;
    private final List<Asiento> asientos = new ArrayList<>();

    public Sala(int numero, String nombre, TipoSala tipo, int filas, int columnas, int filasPreferenciales) {
        if (filas <= 0 || filas > MAX_FILAS || columnas <= 0) {
            throw new DatoInvalidoException("La sala debe tener entre 1 y " + MAX_FILAS + " filas y al menos una columna");
        }
        if (filasPreferenciales < 0 || filasPreferenciales > filas) {
            throw new DatoInvalidoException("Las filas preferenciales deben estar entre 0 y " + filas);
        }
        this.numero = numero;
        this.nombre = Validaciones.texto(nombre, "nombre de la sala");
        this.tipo = Validaciones.requerido(tipo, "tipo de sala");
        this.filas = filas;
        this.columnas = columnas;
        for (int f = 0; f < filas; f++) {
            TipoAsiento tipoAsiento = TipoAsiento.GENERAL;
            if (f >= filas - filasPreferenciales) {
                tipoAsiento = TipoAsiento.PREFERENCIAL;
            }
            char letraFila = (char) ('A' + f);
            for (int c = 1; c <= columnas; c++) {
                asientos.add(new Asiento(letraFila, c, tipoAsiento));
            }
        }
    }

    public Asiento buscarAsiento(String codigo) {
        for (Asiento asiento : asientos) {
            if (asiento.getCodigo().equalsIgnoreCase(codigo)) {
                return asiento;
            }
        }
        throw new EntidadNoEncontradaException("La sala " + nombre + " no tiene el asiento " + codigo);
    }

    @Override
    public String getIdentificador() {
        return String.valueOf(numero);
    }

    public int getCapacidad() {
        return asientos.size();
    }

    public int getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoSala getTipo() {
        return tipo;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public List<Asiento> getAsientos() {
        return Collections.unmodifiableList(asientos);
    }

    @Override
    public String toString() {
        return nombre + " (" + tipo.getEtiqueta() + ")";
    }
}
