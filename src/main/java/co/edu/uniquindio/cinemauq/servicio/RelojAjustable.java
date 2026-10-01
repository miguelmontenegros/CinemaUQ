package co.edu.uniquindio.cinemauq.servicio;

import java.time.Duration;
import java.time.LocalDateTime;

public class RelojAjustable implements Reloj {

    private LocalDateTime actual;

    public RelojAjustable(LocalDateTime inicial) {
        this.actual = inicial;
    }

    @Override
    public LocalDateTime ahora() {
        return actual;
    }

    public void fijar(LocalDateTime nuevaHora) {
        this.actual = nuevaHora;
    }

    public void avanzar(Duration duracion) {
        this.actual = actual.plus(duracion);
    }
}
