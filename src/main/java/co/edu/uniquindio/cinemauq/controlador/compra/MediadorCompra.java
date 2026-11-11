package co.edu.uniquindio.cinemauq.controlador.compra;

public interface MediadorCompra {

    String FUNCION_ELEGIDA = "funcionElegida";
    String ASIENTOS_CAMBIARON = "asientosCambiaron";
    String OPCIONES_CAMBIARON = "opcionesCambiaron";
    String CARRITO_CAMBIO = "carritoCambio";
    String PROMOCION_CAMBIO = "promocionCambio";

    void notificar(ComponenteCompra emisor, String evento);
}
