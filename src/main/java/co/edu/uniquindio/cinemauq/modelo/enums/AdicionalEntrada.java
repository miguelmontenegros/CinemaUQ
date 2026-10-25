package co.edu.uniquindio.cinemauq.modelo.enums;

public enum AdicionalEntrada {
    GAFAS_3D("Gafas 3D", 4_000),
    SILLA_VIBRATORIA("Silla con vibración", 9_000);

    private final String nombre;
    private final long valor;

    AdicionalEntrada(String nombre, long valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    public String getNombre() {
        return nombre;
    }

    public long getValor() {
        return valor;
    }

    public boolean permitidoEn(TipoSala tipoSala) {
        if (this == GAFAS_3D) {
            return tipoSala == TipoSala.TRES_D || tipoSala == TipoSala.IMAX;
        }
        return true;
    }
}