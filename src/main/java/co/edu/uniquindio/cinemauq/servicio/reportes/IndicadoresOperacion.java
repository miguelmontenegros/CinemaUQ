package co.edu.uniquindio.cinemauq.servicio.reportes;

import co.edu.uniquindio.cinemauq.servicio.eventos.EstadisticasOperacion;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class IndicadoresOperacion {

    public static final int DIAS_GRAFICA = 14;

    private final EstadisticasOperacion estadisticas;
    private final Map<String, Long> ingresosPorPelicula;
    private final Map<String, Integer> entradasPorTipo;
    private final Map<String, Long> unidadesConfiteria;
    private final Map<String, Double> ocupacionPorSala;
    private final Map<LocalDate, Long> ventasPorDia;
    private final Map<LocalDate, Long> reembolsosPorDia;
    private final List<String> actividadReciente;

    public IndicadoresOperacion(GeneradorReportes generador, List<String> actividadReciente) {
        this.estadisticas = generador.getEstadisticas();
        this.ingresosPorPelicula = generador.ingresosPorPelicula();
        this.entradasPorTipo = generador.entradasPorTipo();
        this.unidadesConfiteria = generador.unidadesConfiteria();
        this.ocupacionPorSala = generador.ocupacionPorSala();
        this.ventasPorDia = generador.ventasPorDia(DIAS_GRAFICA);
        this.reembolsosPorDia = generador.reembolsosPorDia(DIAS_GRAFICA);
        this.actividadReciente = actividadReciente;
    }

    public EstadisticasOperacion getEstadisticas() {
        return estadisticas;
    }

    public Map<String, Long> getIngresosPorPelicula() {
        return Collections.unmodifiableMap(ingresosPorPelicula);
    }

    public Map<String, Integer> getEntradasPorTipo() {
        return Collections.unmodifiableMap(entradasPorTipo);
    }

    public Map<String, Long> getUnidadesConfiteria() {
        return Collections.unmodifiableMap(unidadesConfiteria);
    }

    public Map<String, Double> getOcupacionPorSala() {
        return Collections.unmodifiableMap(ocupacionPorSala);
    }

    public Map<LocalDate, Long> getVentasPorDia() {
        return Collections.unmodifiableMap(ventasPorDia);
    }

    public Map<LocalDate, Long> getReembolsosPorDia() {
        return Collections.unmodifiableMap(reembolsosPorDia);
    }

    public List<String> getActividadReciente() {
        return Collections.unmodifiableList(actividadReciente);
    }
}
