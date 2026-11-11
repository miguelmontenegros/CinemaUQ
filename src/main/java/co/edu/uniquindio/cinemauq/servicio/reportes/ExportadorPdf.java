package co.edu.uniquindio.cinemauq.servicio.reportes;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExportadorPdf implements ExportadorReporte {

    private static final double ANCHO_CARACTER_COURIER = 0.6;
    private static final double TAMANO_TABLA_MAXIMO = 9;
    private static final double TAMANO_TABLA_MINIMO = 6;

    @Override
    public String getNombre() {
        return "Documento PDF";
    }

    @Override
    public String getExtension() {
        return "pdf";
    }

    @Override
    public void exportar(Reporte reporte, File destino) throws IOException {
        DocumentoPdf documento = new DocumentoPdf();
        documento.escribir(TextoReporte.encabezado(reporte), DocumentoPdf.Fuente.TITULO, 16, 0);
        documento.escribir(TextoReporte.fechaGeneracion(reporte), DocumentoPdf.Fuente.NORMAL, 9, 2);
        if (!reporte.getDescripcion().isEmpty()) {
            documento.escribir(reporte.getDescripcion(), DocumentoPdf.Fuente.NORMAL, 10, 4);
        }
        for (SeccionReporte seccion : reporte.getSecciones()) {
            documento.escribir(seccion.getTitulo(), DocumentoPdf.Fuente.TITULO, 12, 14);
            List<String> tabla = TextoReporte.tabla(seccion);
            double tamano = tamanoQueCabe(tabla, documento.getAnchoUtil());
            double espacio = 4;
            for (String linea : tabla) {
                documento.escribir(linea, DocumentoPdf.Fuente.MONOESPACIADA, tamano, espacio);
                espacio = 0;
            }
            for (String nota : seccion.getNotas()) {
                documento.escribir(nota, DocumentoPdf.Fuente.NORMAL, 9, 3);
            }
        }
        documento.guardar(destino);
    }

    private double tamanoQueCabe(List<String> lineas, double anchoUtil) {
        int mayor = 1;
        for (String linea : lineas) {
            mayor = Math.max(mayor, linea.length());
        }
        double tamano = anchoUtil / (mayor * ANCHO_CARACTER_COURIER);
        return Math.max(TAMANO_TABLA_MINIMO, Math.min(TAMANO_TABLA_MAXIMO, tamano));
    }
}
