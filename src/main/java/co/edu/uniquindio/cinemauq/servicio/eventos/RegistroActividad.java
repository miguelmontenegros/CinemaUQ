package co.edu.uniquindio.cinemauq.servicio.eventos;

import co.edu.uniquindio.cinemauq.util.Formatos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegistroActividad implements ObservadorOperaciones {

    private final List<String> lineas = new ArrayList<>();

    @Override
    public void alOcurrir(EventoOperacion evento) {
        lineas.add(evento.getMomento().format(Formatos.FECHA_HORA) + " - " + evento.getTipo().getEtiqueta()
                + " - " + evento.getDescripcion() + " - " + Formatos.pesos(evento.getValor()));
    }

    public List<String> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    public List<String> ultimas(int cantidad) {
        List<String> resultado = new ArrayList<>();
        for (int i = lineas.size() - 1; i >= 0 && resultado.size() < cantidad; i--) {
            resultado.add(lineas.get(i));
        }
        return resultado;
    }
}