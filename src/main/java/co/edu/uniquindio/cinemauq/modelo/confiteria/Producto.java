package co.edu.uniquindio.cinemauq.modelo.confiteria;

import co.edu.uniquindio.cinemauq.modelo.enums.CategoriaProducto;
import co.edu.uniquindio.cinemauq.util.Validaciones;

public class Producto extends ItemConfiteria {

    private final CategoriaProducto categoria;

    public Producto(String codigo, String nombre, CategoriaProducto categoria, long precio, int costoPuntos) {
        super(codigo, nombre, precio, costoPuntos);
        this.categoria = Validaciones.requerido(categoria, "categoría");
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    @Override
    public long getValorSeparado() {
        return getPrecio();
    }

    @Override
    public int contarProductos() {
        return 1;
    }

    @Override
    public String getDescripcion() {
        return getNombre();
    }
}
