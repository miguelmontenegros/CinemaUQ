package co.edu.uniquindio.cinemauq.modelo.confiteria;

import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Combo extends ItemConfiteria {

    private final List<ItemCombo> contenido;

    public Combo(String codigo, String nombre, long precio, int costoPuntos, List<ItemCombo> contenido) {
        super(codigo, nombre, precio, costoPuntos);
        if (contenido == null) {
            throw new DatoInvalidoException("Un combo debe tener al menos dos productos");
        }
        int unidades = 0;
        long valorSeparado = 0;
        for (ItemCombo item : contenido) {
            unidades = unidades + item.getCantidad();
            valorSeparado = valorSeparado + item.getValorIndividual();
        }
        if (unidades < 2) {
            throw new DatoInvalidoException("Un combo debe tener al menos dos productos");
        }
        if (precio >= valorSeparado) {
            throw new DatoInvalidoException("El precio del combo debe ser menor que " + valorSeparado);
        }
        this.contenido = new ArrayList<>(contenido);
    }

    public long getAhorro() {
        long valorSeparado = 0;
        for (ItemCombo item : contenido) {
            valorSeparado = valorSeparado + item.getValorIndividual();
        }
        return valorSeparado - getPrecio();
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