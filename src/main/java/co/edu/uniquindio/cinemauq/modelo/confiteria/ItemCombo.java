package co.edu.uniquindio.cinemauq.modelo.confiteria;

import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.util.Validaciones;

public class ItemCombo {

    private final ItemConfiteria componente;
    private final int cantidad;

    public ItemCombo(ItemConfiteria componente, int cantidad) {
        this.componente = Validaciones.requerido(componente, "componente");
        if (cantidad <= 0) {
            throw new DatoInvalidoException("La cantidad de " + componente.getNombre() + " en el combo debe ser positiva");
        }
        this.cantidad = cantidad;
    }

    public long getValorSeparado() {
        return componente.getValorSeparado() * cantidad;
    }

    public int contarProductos() {
        return componente.contarProductos() * cantidad;
    }

    public ItemConfiteria getComponente() {
        return componente;
    }

    public int getCantidad() {
        return cantidad;
    }

    @Override
    public String toString() {
        return cantidad + " x " + componente.getDescripcion();
    }
}
