package co.edu.uniquindio.cinemauq.modelo.confiteria;

import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Combo extends ItemConfiteria {

    private final List<ItemCombo> contenido = new ArrayList<>();

    public Combo(String codigo, String nombre, long precio, int costoPuntos, List<ItemCombo> contenido) {
        super(codigo, nombre, precio, costoPuntos);
        if (contenido == null || contenido.isEmpty()) {
            throw new DatoInvalidoException("Un combo debe tener al menos dos productos");
        }
        for (ItemCombo item : contenido) {
            if (item.getComponente() == this) {
                throw new DatoInvalidoException("Un combo no puede contenerse a sí mismo");
            }
            this.contenido.add(item);
        }
        if (contarProductos() < 2) {
            throw new DatoInvalidoException("Un combo debe tener al menos dos productos");
        }
        if (precio >= getValorSeparado()) {
            throw new DatoInvalidoException("El precio del combo debe ser menor que " + getValorSeparado());
        }
    }

    @Override
    public long getValorSeparado() {
        long total = 0;
        for (ItemCombo item : contenido) {
            total = total + item.getValorSeparado();
        }
        return total;
    }

    @Override
    public int contarProductos() {
        int total = 0;
        for (ItemCombo item : contenido) {
            total = total + item.contarProductos();
        }
        return total;
    }

    public long getAhorro() {
        return getValorSeparado() - getPrecio();
    }

    public List<ItemCombo> getContenido() {
        return Collections.unmodifiableList(contenido);
    }

    @Override
    public String getDescripcion() {
        String texto = getNombre() + " (";
        for (int i = 0; i < contenido.size(); i++) {
            if (i > 0) {
                texto = texto + ", ";
            }
            texto = texto + contenido.get(i).toString();
        }
        return texto + ")";
    }
}