package co.edu.uniquindio.cinemauq.modelo.compra;

import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.modelo.contratos.Identificable;
import co.edu.uniquindio.cinemauq.modelo.enums.AlcancePromocion;
import co.edu.uniquindio.cinemauq.util.Validaciones;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;


public class Promocion implements Identificable {

    public static final int DESCUENTO_MAXIMO = 50;

    private final String codigo;
    private final String nombre;
    private final int porcentaje;
    private final AlcancePromocion alcance;
    private final LocalDate inicio;
    private final LocalDate fin;
    private final Set<DayOfWeek> dias;

    public Promocion(String codigo, String nombre, int porcentaje, AlcancePromocion alcance,
                     LocalDate inicio, LocalDate fin, Set<DayOfWeek> dias) {
        if (porcentaje <= 0 || porcentaje > DESCUENTO_MAXIMO) {
            throw new DatoInvalidoException("El descuento debe estar entre 1 % y " + DESCUENTO_MAXIMO + " %");
        }
        if (fin.isBefore(inicio)) {
            throw new DatoInvalidoException("La fecha final de la promoción es anterior a la inicial");
        }
        this.codigo = Validaciones.texto(codigo, "código");
        this.nombre = Validaciones.texto(nombre, "nombre");
        this.porcentaje = porcentaje;
        this.alcance = Validaciones.requerido(alcance, "alcance");
        this.inicio = inicio;
        this.fin = fin;
        if (dias == null || dias.isEmpty()) {
            this.dias = EnumSet.allOf(DayOfWeek.class);
        } else {
            this.dias = EnumSet.copyOf(dias);
        }
    }

    public boolean estaVigente(LocalDateTime momento) {
        LocalDate dia = momento.toLocalDate();
        if (dia.isBefore(inicio) || dia.isAfter(fin)) {
            return false;
        }
        return dias.contains(dia.getDayOfWeek());
    }

    public long calcularDescuento(long subtotalEntradas, long subtotalConfiteria) {
        long base;
        switch (alcance) {
            case ENTRADAS:
                base = subtotalEntradas;
                break;
            case CONFITERIA:
                base = subtotalConfiteria;
                break;
            default:
                base = subtotalEntradas + subtotalConfiteria;
                break;
        }
        return Math.round(base * porcentaje / 100.0);
    }

    @Override
    public String getIdentificador() {
        return codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public AlcancePromocion getAlcance() {
        return alcance;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public LocalDate getFin() {
        return fin;
    }

    @Override
    public String toString() {
        return nombre + " (-" + porcentaje + " % " + alcance.name().toLowerCase() + ")";
    }
}