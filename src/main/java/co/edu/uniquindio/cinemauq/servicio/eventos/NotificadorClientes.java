package co.edu.uniquindio.cinemauq.servicio.eventos;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.util.Formatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificadorClientes implements ObservadorOperaciones {

    private final Map<Cliente, List<String>> bandejas = new HashMap<>();

    @Override
    public void alOcurrir(EventoOperacion evento) {
        Cliente cliente = evento.getCliente();
        if (cliente == null) {
            return;
        }
        String fecha = evento.getMomento().format(Formatos.FECHA_HORA) + " - ";
        Compra compra = evento.getCompra();
        switch (evento.getTipo()) {
            case COMPRA_CONFIRMADA:
                agregar(cliente, fecha + "Su compra " + compra.getCodigo() + " para " + compra.getFuncion()
                        + " quedó confirmada por " + Formatos.pesos(evento.getValor()) + ". Ganó "
                        + compra.getPuntosGanados() + " puntos.");
                break;
            case COMPRA_CANCELADA:
                agregar(cliente, fecha + "La compra " + compra.getCodigo() + " fue cancelada ("
                        + compra.getReembolso().getMotivo().toLowerCase() + "). Se devolvieron "
                        + Formatos.pesos(evento.getValor()) + " a su tarjeta.");
                break;
            case RECARGA_REALIZADA:
                agregar(cliente, fecha + "Su tarjeta fue recargada con " + Formatos.pesos(evento.getValor()) + ".");
                break;
            default:
                break;
        }
    }

    private void agregar(Cliente cliente, String mensaje) {
        List<String> bandeja = bandejas.get(cliente);
        if (bandeja == null) {
            bandeja = new ArrayList<>();
            bandejas.put(cliente, bandeja);
        }
        bandeja.add(mensaje);
    }

    public List<String> notificacionesDe(Cliente cliente) {
        List<String> resultado = new ArrayList<>();
        List<String> bandeja = bandejas.get(cliente);
        if (bandeja != null) {
            for (int i = bandeja.size() - 1; i >= 0; i--) {
                resultado.add(bandeja.get(i));
            }
        }
        return resultado;
    }
}