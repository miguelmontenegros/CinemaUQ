package co.edu.uniquindio.cinemauq.modelo.confiteria;

import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.util.Validaciones;

public class ItemCombo {

    private final Producto producto;
    private final int cantidad;

    public ItemCombo(Producto producto, int cantidad) {
        this.producto = Validaciones.requerido(producto, "producto");
        if (cantidad <= 0) {
            throw new DatoInvalidoException("La cantidad de " + producto.getNombre() + " en el combo debe ser positiva");
        }
        this.cantidad = cantidad;
    }

    public long getValorIndividual() {
        return producto.getPrecio() * cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    @Override
    public String toString() {
        return cantidad + " x " + producto.getNombre();
    }
}