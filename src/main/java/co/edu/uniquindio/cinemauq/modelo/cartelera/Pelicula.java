package co.edu.uniquindio.cinemauq.modelo.cartelera;

import co.edu.uniquindio.cinemauq.modelo.contratos.Identificable;
import co.edu.uniquindio.cinemauq.modelo.enums.ClasificacionEdad;
import co.edu.uniquindio.cinemauq.modelo.enums.GeneroPelicula;
import co.edu.uniquindio.cinemauq.util.Validaciones;

public class Pelicula implements Identificable {

    private final String codigo;
    private final String titulo;
    private final GeneroPelicula genero;
    private final ClasificacionEdad clasificacion;
    private final int duracionMinutos;
    private final String sinopsis;
    private boolean enCartelera = true;

    public Pelicula(String codigo, String titulo, GeneroPelicula genero, ClasificacionEdad clasificacion,
                    int duracionMinutos, String sinopsis) {
        this.codigo = Validaciones.texto(codigo, "código");
        this.titulo = Validaciones.texto(titulo, "título");
        this.genero = Validaciones.requerido(genero, "género");
        this.clasificacion = Validaciones.requerido(clasificacion, "clasificación");
        this.duracionMinutos = (int) Validaciones.positivo(duracionMinutos, "duración");
        if (sinopsis == null) {
            this.sinopsis = "";
        } else {
            this.sinopsis = sinopsis;
        }
    }

    public void retirarDeCartelera() {
        enCartelera = false;
    }

    public void volverACartelera() {
        enCartelera = true;
    }

    @Override
    public String getIdentificador() {
        return codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public GeneroPelicula getGenero() {
        return genero;
    }

    public ClasificacionEdad getClasificacion() {
        return clasificacion;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public boolean isEnCartelera() {
        return enCartelera;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
