package co.edu.uniquindio.cinemauq.modelo.cartelera;

import co.edu.uniquindio.cinemauq.excepciones.EntidadNoEncontradaException;
import co.edu.uniquindio.cinemauq.excepciones.FuncionNoDisponibleException;
import co.edu.uniquindio.cinemauq.modelo.GeneradorCodigos;
import co.edu.uniquindio.cinemauq.modelo.contratos.Identificable;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoAsiento;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoFuncion;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoCodigo;
import co.edu.uniquindio.cinemauq.util.Formatos;
import co.edu.uniquindio.cinemauq.util.Validaciones;

import java.time.LocalDateTime;
import java.util.*;

public class Funcion implements Cloneable, Identificable {

    public static final int MINUTOS_LIMPIEZA = 20;

    private String codigo;
    private final Pelicula pelicula;
    private final Sala sala;
    private LocalDateTime fechaHora;
    private final long precioBase;
    private EstadoFuncion estado = EstadoFuncion.PROGRAMADA;
    private Map<String, AsientoFuncion> mapaAsientos = new LinkedHashMap<>();

    public Funcion(Pelicula pelicula, Sala sala, LocalDateTime fechaHora, long precioBase) {
        this.codigo = GeneradorCodigos.getInstancia().siguiente(TipoCodigo.FUNCION);
        this.pelicula = Validaciones.requerido(pelicula, "película");
        this.sala = Validaciones.requerido(sala, "sala");
        this.fechaHora = Validaciones.requerido(fechaHora, "fecha y hora");
        this.precioBase = Validaciones.positivo(precioBase, "precio base");
        for (Asiento asiento : sala.getAsientos()) {
            mapaAsientos.put(asiento.getCodigo(), new AsientoFuncion(asiento));
        }
    }

    @Override
    public Funcion clone() {
        try {
            Funcion copia = (Funcion) super.clone();
            copia.codigo = GeneradorCodigos.getInstancia().siguiente(TipoCodigo.FUNCION);
            copia.estado = EstadoFuncion.PROGRAMADA;
            copia.mapaAsientos = new LinkedHashMap<>();
            for (AsientoFuncion af : this.mapaAsientos.values()) {
                copia.mapaAsientos.put(af.getAsiento().getCodigo(), af.clone());
            }
            return copia;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Funcion debe ser clonable", e);
        }
    }

    public Funcion clonarPara(LocalDateTime nuevaFechaHora) {
        Funcion copia = clone();
        copia.fechaHora = Validaciones.requerido(nuevaFechaHora, "fecha y hora");
        return copia;
    }


    public boolean admiteVentas(LocalDateTime ahora) {
        return estado == EstadoFuncion.PROGRAMADA && ahora.isBefore(fechaHora);
    }

    public void validarAdmiteVentas(LocalDateTime ahora) {
        if (estado == EstadoFuncion.CANCELADA) {
            throw new FuncionNoDisponibleException("La función " + codigo + " fue cancelada");
        }
        if (!ahora.isBefore(fechaHora)) {
            throw new FuncionNoDisponibleException("La función " + codigo + " ya comenzó");
        }
    }

    public LocalDateTime getHoraFin() {
        return fechaHora.plusMinutes(pelicula.getDuracionMinutos());
    }

    public boolean seCruzaCon(Funcion otra) {
        if (otra == this || otra.sala.getNumero() != sala.getNumero() || otra.estado == EstadoFuncion.CANCELADA) {
            return false;
        }
        LocalDateTime finEste = getHoraFin().plusMinutes(MINUTOS_LIMPIEZA);
        LocalDateTime finOtra = otra.getHoraFin().plusMinutes(MINUTOS_LIMPIEZA);
        return fechaHora.isBefore(finOtra) && otra.fechaHora.isBefore(finEste);
    }

    public AsientoFuncion getAsiento(String codigoAsiento) {
        AsientoFuncion af = mapaAsientos.get(codigoAsiento.toUpperCase());
        if (af == null) {
            throw new EntidadNoEncontradaException("La sala " + sala.getNombre() + " no tiene el asiento " + codigoAsiento);
        }
        return af;
    }

    public void ocuparAsiento(String codigoAsiento) {
        getAsiento(codigoAsiento).vender();
    }

    public void liberarAsiento(String codigoAsiento) {
        getAsiento(codigoAsiento).liberar();
    }

    public void bloquearAsiento(String codigoAsiento) {
        getAsiento(codigoAsiento).bloquear();
    }

    public void cancelar() {
        estado = EstadoFuncion.CANCELADA;
    }

    public int contarAsientos(EstadoAsiento estadoBuscado) {
        int contador = 0;
        for (AsientoFuncion af : mapaAsientos.values()) {
            if (af.getEstado() == estadoBuscado) {
                contador++;
            }
        }
        return contador;
    }

    public double getPorcentajeOcupacion() {
        return 100.0 * contarAsientos(EstadoAsiento.VENDIDO) / mapaAsientos.size();
    }

    public boolean compartenMapa(Funcion otra) {
        return this.mapaAsientos == otra.mapaAsientos;
    }


    public List<AsientoFuncion> getAsientos() {
        return Collections.unmodifiableList(new ArrayList<>(mapaAsientos.values()));
    }

    @Override
    public String getIdentificador() {
        return codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public long getPrecioBase() {
        return precioBase;
    }

    public EstadoFuncion getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return pelicula.getTitulo() + " · " + sala + " · " + fechaHora.format(Formatos.FECHA_HORA);
    }
}
