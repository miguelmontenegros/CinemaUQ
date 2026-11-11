package co.edu.uniquindio.cinemauq.servicio.eventos;

public enum TipoEvento {
    COMPRA_CONFIRMADA("Compra confirmada"),
    COMPRA_CANCELADA("Compra cancelada"),
    FUNCION_CANCELADA("Función cancelada"),
    RECARGA_REALIZADA("Recarga realizada");

    private final String etiqueta;

    TipoEvento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}