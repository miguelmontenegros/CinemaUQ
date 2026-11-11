package co.edu.uniquindio.cinemauq.servicio.reportes;

public enum TipoReporte {
    RESUMEN_GENERAL("Resumen general de operación"),
    VENTAS_POR_PELICULA("Ventas por película"),
    OCUPACION_FUNCIONES("Ocupación de funciones"),
    CONFITERIA("Ventas de confitería"),
    PROGRAMA_PUNTOS("Programa de puntos"),
    CANCELACIONES("Cancelaciones y reembolsos");

    private final String titulo;

    TipoReporte(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
