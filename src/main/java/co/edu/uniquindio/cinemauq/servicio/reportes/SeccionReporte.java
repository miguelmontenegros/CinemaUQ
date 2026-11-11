package co.edu.uniquindio.cinemauq.servicio.reportes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeccionReporte {

    private final String titulo;
    private final String[] columnas;
    private final List<String[]> filas = new ArrayList<>();
    private final List<String> notas = new ArrayList<>();

    public SeccionReporte(String titulo, String... columnas) {
        this.titulo = titulo;
        this.columnas = columnas.clone();
    }

    public SeccionReporte agregarFila(String... valores) {
        String[] fila = new String[columnas.length];
        for (int i = 0; i < columnas.length; i++) {
            if (i < valores.length && valores[i] != null) {
                fila[i] = valores[i];
            } else {
                fila[i] = "";
            }
        }
        filas.add(fila);
        return this;
    }

    public SeccionReporte agregarNota(String nota) {
        notas.add(nota);
        return this;
    }

    public String getTitulo() {
        return titulo;
    }

    public String[] getColumnas() {
        return columnas.clone();
    }

    public List<String[]> getFilas() {
        return Collections.unmodifiableList(filas);
    }

    public List<String> getNotas() {
        return Collections.unmodifiableList(notas);
    }

    public boolean tieneTabla() {
        return columnas.length > 0;
    }
}
