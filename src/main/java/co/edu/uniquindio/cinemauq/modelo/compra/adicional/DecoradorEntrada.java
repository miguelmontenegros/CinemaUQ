package co.edu.uniquindio.cinemauq.modelo.compra.adicional;

import co.edu.uniquindio.cinemauq.modelo.compra.Entrada;
import co.edu.uniquindio.cinemauq.modelo.enums.AdicionalEntrada;

public abstract class DecoradorEntrada extends Entrada {

    private final Entrada entrada;
    private final AdicionalEntrada adicional;

    protected DecoradorEntrada(Entrada entrada, AdicionalEntrada adicional) {
        super(entrada.getCodigo(), entrada.getFuncion(), entrada.getAsiento());
        this.entrada = entrada;
        this.adicional = adicional;
    }

    @Override
    public long getPrecio() {
        return entrada.getPrecio() + adicional.getValor();
    }

    @Override
    public String getTipo() {
        return entrada.getTipo() + " + " + adicional.getNombre();
    }

    @Override
    public boolean isCortesia() {
        return entrada.isCortesia();
    }

    @Override
    public int getCostoPuntos() {
        return entrada.getCostoPuntos();
    }

    public Entrada getEntradaDecorada() {
        return entrada;
    }

    public AdicionalEntrada getAdicional() {
        return adicional;
    }
}
