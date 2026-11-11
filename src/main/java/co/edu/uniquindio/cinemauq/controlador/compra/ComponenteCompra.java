package co.edu.uniquindio.cinemauq.controlador.compra;

public abstract class ComponenteCompra {

    protected MediadorCompra mediador;

    public void setMediador(MediadorCompra mediador) {
        this.mediador = mediador;
    }

    protected void avisar(String evento) {
        if (mediador != null) {
            mediador.notificar(this, evento);
        }
    }
}
