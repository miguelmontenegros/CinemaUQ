package co.edu.uniquindio.cinemauq.repositorio;

import co.edu.uniquindio.cinemauq.modelo.contratos.Identificable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RepositorioMemoria<T extends Identificable> implements Repositorio<T> {

    private final Map<String, T> datos = new LinkedHashMap<>();

    @Override
    public void guardar(T entidad) {
        datos.put(normalizar(entidad.getIdentificador()), entidad);
    }

    @Override
    public T buscar(String id) {
        return datos.get(normalizar(id));
    }

    @Override
    public boolean existe(String id) {
        return datos.containsKey(normalizar(id));
    }

    @Override
    public List<T> listar() {
        return new ArrayList<>(datos.values());
    }

    private String normalizar(String id) {
        if (id == null) {
            return "";
        }
        return id.trim().toLowerCase();
    }
}