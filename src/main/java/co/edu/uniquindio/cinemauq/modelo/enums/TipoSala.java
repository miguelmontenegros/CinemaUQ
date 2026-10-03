package co.edu.uniquindio.cinemauq.modelo.enums;

public enum TipoSala {
    DOS_D("2D"), TRES_D("3D"), IMAX("IMAX");

    private final String etiqueta;

    TipoSala(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
