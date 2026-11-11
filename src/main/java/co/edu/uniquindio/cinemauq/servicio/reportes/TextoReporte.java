package co.edu.uniquindio.cinemauq.servicio.reportes;

import co.edu.uniquindio.cinemauq.util.Formatos;

import java.util.ArrayList;
import java.util.List;

public final class TextoReporte {

    private static final String SEPARADOR_COLUMNAS = "  ";

    private TextoReporte() {
    }

    public static String encabezado(Reporte reporte) {
        return "CinemaUQ - " + reporte.getTitulo();
    }

    public static String fechaGeneracion(Reporte reporte) {
        return "Generado: " + reporte.getGeneradoEn().format(Formatos.FECHA_HORA);
    }

    public static List<String> tabla(SeccionReporte seccion) {
        List<String> lineas = new ArrayList<>();
        if (!seccion.tieneTabla()) {
            return lineas;
        }
        String[] columnas = seccion.getColumnas();
        int[] anchos = new int[columnas.length];
        for (int i = 0; i < columnas.length; i++) {
            anchos[i] = columnas[i].length();
        }
        for (String[] fila : seccion.getFilas()) {
            for (int i = 0; i < fila.length; i++) {
                anchos[i] = Math.max(anchos[i], fila[i].length());
            }
        }
        lineas.add(fila(columnas, anchos));
        String[] guiones = new String[columnas.length];
        for (int i = 0; i < columnas.length; i++) {
            guiones[i] = "-".repeat(anchos[i]);
        }
        lineas.add(fila(guiones, anchos));
        for (String[] valores : seccion.getFilas()) {
            lineas.add(fila(valores, anchos));
        }
        return lineas;
    }

    private static String fila(String[] valores, int[] anchos) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                texto.append(SEPARADOR_COLUMNAS);
            }
            String valor = valores[i];
            String relleno = " ".repeat(anchos[i] - valor.length());
            if (esNumerico(valor)) {
                texto.append(relleno).append(valor);
            } else {
                texto.append(valor).append(relleno);
            }
        }
        return texto.toString().stripTrailing();
    }

    private static boolean esNumerico(String valor) {
        if (valor.isEmpty()) {
            return false;
        }
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            if (!Character.isDigit(c) && c != '$' && c != '.' && c != ',' && c != '%' && c != ' ' && c != '-') {
                return false;
            }
        }
        return true;
    }

    public static String completo(Reporte reporte) {
        StringBuilder texto = new StringBuilder();
        String titulo = encabezado(reporte);
        texto.append(titulo).append(System.lineSeparator());
        texto.append("=".repeat(titulo.length())).append(System.lineSeparator());
        texto.append(fechaGeneracion(reporte)).append(System.lineSeparator());
        if (!reporte.getDescripcion().isEmpty()) {
            texto.append(reporte.getDescripcion()).append(System.lineSeparator());
        }
        for (SeccionReporte seccion : reporte.getSecciones()) {
            texto.append(System.lineSeparator());
            texto.append(seccion.getTitulo()).append(System.lineSeparator());
            texto.append("-".repeat(seccion.getTitulo().length())).append(System.lineSeparator());
            for (String linea : tabla(seccion)) {
                texto.append(linea).append(System.lineSeparator());
            }
            for (String nota : seccion.getNotas()) {
                texto.append(nota).append(System.lineSeparator());
            }
        }
        return texto.toString();
    }
}
