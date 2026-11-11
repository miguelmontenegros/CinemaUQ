package co.edu.uniquindio.cinemauq.servicio.eventos;

import java.util.ArrayList;
import java.util.List;

public class PublicadorEventos {

    private final List<ObservadorOperaciones> observadores = new ArrayList<>();

    public void suscribir(ObservadorOperaciones observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void desuscribir(ObservadorOperaciones observador) {
        observadores.remove(observador);
    }

    public void publicar(EventoOperacion evento) {
        for (ObservadorOperaciones observador : new ArrayList<>(observadores)) {
            observador.alOcurrir(evento);
        }
    }

    public int cantidadObservadores() {
        return observadores.size();
    }
}