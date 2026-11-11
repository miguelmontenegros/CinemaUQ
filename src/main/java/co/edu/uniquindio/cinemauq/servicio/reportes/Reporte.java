package co.edu.uniquindio.cinemauq.servicio.reportes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reporte {

    private final TipoReporte tipo;
    private final LocalDateTime generadoEn;
    private final String descripcion;
    private final List<SeccionReporte> secciones = new ArrayList<>();

    public Reporte(TipoReporte tipo, LocalDateTime generadoEn, String descripcion) {
        this.tipo = tipo;
        this.generadoEn = generadoEn;
        this.descripcion = descripcion;
    }

    public SeccionReporte nuevaSeccion(String titulo, String... columnas) {
        SeccionReporte seccion = new SeccionReporte(titulo, columnas);
        secciones.add(seccion);
        return seccion;
    }

    public TipoReporte getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return tipo.getTitulo();
    }

    public LocalDateTime getGeneradoEn() {
        return generadoEn;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public List<SeccionReporte> getSecciones() {
        return Collections.unmodifiableList(secciones);
    }
}
