package co.edu.uniquindio.cinemauq.modelo.compra;

import co.edu.uniquindio.cinemauq.modelo.confiteria.ItemConfiteria;

public class LineaConfiteria {

    private final ItemConfiteria item;
    private final int cantidad;
    private final boolean redimidaConPuntos;

    public LineaConfiteria(ItemConfiteria item, int cantidad, boolean redimidaConPuntos) {
        this.item = item;
        this.cantidad = cantidad;
        this.redimidaConPuntos = redimidaConPuntos;
    }

    public long getSubtotal() {
        if (redimidaConPuntos) {
            return 0;
        }
        return item.getPrecio() * cantidad;
    }

    public int getPuntos() {
        if (redimidaConPuntos) {
            return item.getCostoPuntos() * cantidad;
        }
        return 0;
    }

    public ItemConfiteria getItem() {
        return item;
    }

    public int getCantidad() {
        return cantidad;
    }

    public boolean isRedimidaConPuntos() {
        return redimidaConPuntos;
    }

    @Override
    public String toString() {
        String texto = cantidad + " x " + item.getDescripcion();
        if (redimidaConPuntos) {
            texto = texto + " [puntos]";
        }
        return texto;
    }
}
