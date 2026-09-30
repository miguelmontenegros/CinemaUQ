package co.edu.uniquindio.cinemauq.modelo.enums;

public enum ClasificacionEdad {
    TODO_PUBLICO(0), MAYORES_7(7), MAYORES_12(12), MAYORES_15(15), MAYORES_18(18);

    private final int edadMinima;

    ClasificacionEdad(int edadMinima) {
        this.edadMinima = edadMinima;
    }

    public int getEdadMinima() {
        return edadMinima;
    }
}
