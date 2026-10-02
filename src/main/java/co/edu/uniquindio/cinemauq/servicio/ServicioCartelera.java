package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.ConflictoHorarioException;
import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.excepciones.RegistroDuplicadoException;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Sala;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServicioCartelera {

    private final Repositorio<Pelicula> peliculas;
    private final Repositorio<Sala> salas;
    private final Repositorio<Funcion> funciones;
    private final Reloj reloj;

    public ServicioCartelera(Repositorio<Pelicula> peliculas, Repositorio<Sala> salas, Repositorio<Funcion> funciones, Reloj reloj) {
        this.peliculas = peliculas;
        this.salas = salas;
        this.funciones = funciones;
        this.reloj = reloj;
    }

    public void registrarPelicula(Pelicula pelicula) {
        if (peliculas.existe(pelicula.getCodigo())) {
            throw new RegistroDuplicadoException("Ya existe una película con código " + pelicula.getCodigo());
        }
        peliculas.guardar(pelicula);
    }

    public void registrarSala(Sala sala) {
        if (salas.existe(sala.getIdentificador())) {
            throw new RegistroDuplicadoException("Ya existe la sala número " + sala.getNumero());
        }
        salas.guardar(sala);
    }

    public Funcion programarFuncion(Pelicula pelicula, Sala sala, LocalDateTime fechaHora, long precioBase) {
        if (!pelicula.isEnCartelera()) {
            throw new DatoInvalidoException(pelicula.getTitulo() + " no está en cartelera");
        }
        Funcion funcion = new Funcion(pelicula, sala, fechaHora, precioBase);
        validarYGuardar(funcion);
        return funcion;
    }

    public List<Funcion> replicarFuncion(Funcion plantilla, List<LocalDateTime> nuevasFechas) {
        List<Funcion> creadas = new ArrayList<>();
        for (LocalDateTime fecha : nuevasFechas) {
            Funcion copia = plantilla.clonarPara(fecha);
            validarYGuardar(copia);
            creadas.add(copia);
        }
        return creadas;
    }

    public void bloquearAsiento(Funcion funcion, String codigoAsiento) {
        funcion.bloquearAsiento(codigoAsiento);
    }

    public List<Funcion> funcionesDisponibles() {
        LocalDateTime ahora = reloj.ahora();
        List<Funcion> disponibles = new ArrayList<>();
        for (Funcion funcion : funciones.listar()) {
            if (funcion.admiteVentas(ahora)) {
                disponibles.add(funcion);
            }
        }
        return ordenarPorFecha(disponibles);
    }

    public List<Funcion> listarFunciones() {
        return ordenarPorFecha(funciones.listar());
    }

    public List<Funcion> funcionesDePelicula(String codigoPelicula) {
        List<Funcion> resultado = new ArrayList<>();
        for (Funcion funcion : funciones.listar()) {
            if (funcion.getPelicula().getCodigo().equals(codigoPelicula)) {
                resultado.add(funcion);
            }
        }
        return ordenarPorFecha(resultado);
    }

    public List<Pelicula> listarPeliculas() {
        return peliculas.listar();
    }

    public List<Pelicula> peliculasEnCartelera() {
        List<Pelicula> enCartelera = new ArrayList<>();
        for (Pelicula pelicula : peliculas.listar()) {
            if (pelicula.isEnCartelera()) {
                enCartelera.add(pelicula);
            }
        }
        return enCartelera;
    }

    public List<Sala> listarSalas() {
        return salas.listar();
    }

    private void validarYGuardar(Funcion funcion) {
        if (!funcion.getFechaHora().isAfter(reloj.ahora())) {
            throw new DatoInvalidoException("La función debe programarse en una fecha futura");
        }
        for (Funcion otra : funciones.listar()) {
            if (funcion.seCruzaCon(otra)) {
                throw new ConflictoHorarioException("La sala " + funcion.getSala().getNombre()
                        + " está ocupada por " + otra);
            }
        }
        funciones.guardar(funcion);
    }

    private List<Funcion> ordenarPorFecha(List<Funcion> lista) {
        List<Funcion> ordenadas = new ArrayList<>();
        for (Funcion funcion : lista) {
            int posicion = 0;
            while (posicion < ordenadas.size()
                    && !funcion.getFechaHora().isBefore(ordenadas.get(posicion).getFechaHora())) {
                posicion++;
            }
            ordenadas.add(posicion, funcion);
        }
        return ordenadas;
    }
}
