package co.edu.uniquindio.cinemauq.modelo.confiteria;

import co.edu.uniquindio.cinemauq.modelo.contratos.Identificable;
import co.edu.uniquindio.cinemauq.modelo.contratos.Redimible;
import co.edu.uniquindio.cinemauq.modelo.contratos.Vendible;
import co.edu.uniquindio.cinemauq.util.Validaciones;


public abstract class ItemConfiteria implements Vendible, Redimible, Identificable {

    private final String codigo;
    private final String nombre;
    private final long precio;
    private final int costoPuntos;
    private boolean activo = true;

    protected ItemConfiteria(String codigo, String nombre, long precio, int costoPuntos) {
        this.codigo = Validaciones.texto(codigo, "código");
        this.nombre = Validaciones.texto(nombre, "nombre");
        this.precio = Validaciones.positivo(precio, "precio");
        this.costoPuntos = (int) Validaciones.positivo(costoPuntos, "costo en puntos");
    }

    @Override
    public String getIdentificador() {
        return codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public long getPrecio() {
        return precio;
    }

    @Override
    public int getCostoPuntos() {
        return costoPuntos;
    }

    public boolean isActivo() {
        return activo;
    }

    public void desactivar() {
        activo = false;
    }

    public void activar() {
        activo = true;
    }

    @Override
    public String toString() {
        return nombre;
    }
}