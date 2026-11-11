package co.edu.uniquindio.cinemauq.servicio.reportes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DocumentoPdf {

    public enum Fuente {
        TITULO("F1"), MONOESPACIADA("F2"), NORMAL("F3");

        private final String recurso;

        Fuente(String recurso) {
            this.recurso = recurso;
        }
    }

    public static final double ANCHO_PAGINA = 612;
    public static final double ALTO_PAGINA = 792;
    public static final double MARGEN = 50;

    private static class Linea {
        private final double y;
        private final Fuente fuente;
        private final double tamano;
        private final String texto;

        Linea(double y, Fuente fuente, double tamano, String texto) {
            this.y = y;
            this.fuente = fuente;
            this.tamano = tamano;
            this.texto = texto;
        }
    }

    private final List<List<Linea>> paginas = new ArrayList<>();
    private double cursorY;

    public DocumentoPdf() {
        nuevaPagina();
    }

    public double getAnchoUtil() {
        return ANCHO_PAGINA - 2 * MARGEN;
    }

    public void escribir(String texto, Fuente fuente, double tamano, double espacioAntes) {
        double alto = tamano * 1.3;
        if (cursorY - espacioAntes - alto < MARGEN) {
            nuevaPagina();
            espacioAntes = 0;
        }
        cursorY = cursorY - espacioAntes - alto;
        paginas.get(paginas.size() - 1).add(new Linea(cursorY, fuente, tamano, texto));
    }

    public void nuevaPagina() {
        paginas.add(new ArrayList<>());
        cursorY = ALTO_PAGINA - MARGEN;
    }

    public void guardar(File destino) throws IOException {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        List<Integer> posiciones = new ArrayList<>();
        int totalPaginas = paginas.size();
        int primerObjetoPagina = 6;
        int totalObjetos = 5 + totalPaginas * 2;

        escribirTexto(salida, "%PDF-1.4\n");
        salida.write(new byte[]{'%', (byte) 0xE2, (byte) 0xE3, (byte) 0xCF, (byte) 0xD3, '\n'});

        StringBuilder hijos = new StringBuilder();
        for (int i = 0; i < totalPaginas; i++) {
            hijos.append(primerObjetoPagina + i * 2).append(" 0 R ");
        }
        objeto(salida, posiciones, 1, "<< /Type /Catalog /Pages 2 0 R >>");
        objeto(salida, posiciones, 2, "<< /Type /Pages /Kids [" + hijos.toString().trim() + "] /Count " + totalPaginas + " >>");
        objeto(salida, posiciones, 3, fuente("Helvetica-Bold"));
        objeto(salida, posiciones, 4, fuente("Courier"));
        objeto(salida, posiciones, 5, fuente("Helvetica"));

        for (int i = 0; i < totalPaginas; i++) {
            int numeroPagina = primerObjetoPagina + i * 2;
            int numeroContenido = numeroPagina + 1;
            objeto(salida, posiciones, numeroPagina, "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 "
                    + numero(ANCHO_PAGINA) + " " + numero(ALTO_PAGINA) + "] /Resources << /Font << /F1 3 0 R /F2 4 0 R /F3 5 0 R >> >> /Contents "
                    + numeroContenido + " 0 R >>");
            byte[] contenido = contenidoPagina(paginas.get(i), i + 1, totalPaginas);
            posiciones.add(salida.size());
            escribirTexto(salida, numeroContenido + " 0 obj\n<< /Length " + contenido.length + " >>\nstream\n");
            salida.write(contenido);
            escribirTexto(salida, "\nendstream\nendobj\n");
        }

        int inicioXref = salida.size();
        StringBuilder xref = new StringBuilder("xref\n0 " + (totalObjetos + 1) + "\n0000000000 65535 f \n");
        for (int posicion : posiciones) {
            xref.append(String.format(Locale.ROOT, "%010d 00000 n \n", posicion));
        }
        xref.append("trailer\n<< /Size ").append(totalObjetos + 1).append(" /Root 1 0 R >>\nstartxref\n")
                .append(inicioXref).append("\n%%EOF\n");
        escribirTexto(salida, xref.toString());
        Files.write(destino.toPath(), salida.toByteArray());
    }

    private byte[] contenidoPagina(List<Linea> lineas, int pagina, int totalPaginas) {
        StringBuilder flujo = new StringBuilder();
        for (Linea linea : lineas) {
            texto(flujo, linea.fuente, linea.tamano, MARGEN, linea.y, linea.texto);
        }
        texto(flujo, Fuente.NORMAL, 8, MARGEN, MARGEN / 2, "Página " + pagina + " de " + totalPaginas);
        return flujo.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    private void texto(StringBuilder flujo, Fuente fuente, double tamano, double x, double y, String texto) {
        flujo.append("BT /").append(fuente.recurso).append(' ').append(numero(tamano)).append(" Tf 1 0 0 1 ")
                .append(numero(x)).append(' ').append(numero(y)).append(" Tm (").append(escapar(texto)).append(") Tj ET\n");
    }

    private static String fuente(String nombre) {
        return "<< /Type /Font /Subtype /Type1 /BaseFont /" + nombre + " /Encoding /WinAnsiEncoding >>";
    }

    private static String escapar(String texto) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '\\' || c == '(' || c == ')') {
                resultado.append('\\').append(c);
            } else if (c == ' ' || c == ' ') {
                resultado.append(' ');
            } else if (c > 255) {
                resultado.append('?');
            } else {
                resultado.append(c);
            }
        }
        return resultado.toString();
    }

    private static String numero(double valor) {
        return String.format(Locale.ROOT, "%.2f", valor);
    }

    private static void objeto(ByteArrayOutputStream salida, List<Integer> posiciones, int numero, String cuerpo) throws IOException {
        posiciones.add(salida.size());
        escribirTexto(salida, numero + " 0 obj\n" + cuerpo + "\nendobj\n");
    }

    private static void escribirTexto(ByteArrayOutputStream salida, String texto) throws IOException {
        salida.write(texto.getBytes(StandardCharsets.ISO_8859_1));
    }
}
