package co.edu.uniquindio.cinemauq.util;

import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;

public final class Validaciones {

    private Validaciones() {
    }

    public static String texto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DatoInvalidoException("El campo '" + campo + "' es obligatorio");
        }
        return valor.trim();
    }

    public static <T> T requerido(T valor, String campo) {
        if (valor == null) {
            throw new DatoInvalidoException("El campo '" + campo + "' es obligatorio");
        }
        return valor;
    }

    public static long positivo(long valor, String campo) {
        if (valor <= 0) {
            throw new DatoInvalidoException("El campo '" + campo + "' debe ser mayor que cero");
        }
        return valor;
    }
}
