package co.edu.uniquindio.cinemauq.repositorio;

import co.edu.uniquindio.cinemauq.modelo.contratos.Identificable;

import java.util.List;


public interface Repositorio<T extends Identificable> {

    void guardar(T entidad);

    T buscar(String id);

    boolean existe(String id);

    List<T> listar();
}