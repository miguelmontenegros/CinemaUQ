package co.edu.uniquindio.cinemauq.servicio.reportes;

import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.Entrada;
import co.edu.uniquindio.cinemauq.modelo.compra.LineaConfiteria;
import co.edu.uniquindio.cinemauq.modelo.confiteria.Combo;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoAsiento;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoCompra;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoFuncion;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoAsiento;
import co.edu.uniquindio.cinemauq.modelo.puntos.AcumulacionPuntos;
import co.edu.uniquindio.cinemauq.modelo.puntos.CuentaPuntos;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.servicio.Reloj;
import co.edu.uniquindio.cinemauq.servicio.ServicioAutenticacion;
import co.edu.uniquindio.cinemauq.servicio.ServicioCancelaciones;
import co.edu.uniquindio.cinemauq.servicio.ServicioCartelera;
import co.edu.uniquindio.cinemauq.servicio.ServicioCompras;
import co.edu.uniquindio.cinemauq.servicio.eventos.EstadisticasOperacion;
import co.edu.uniquindio.cinemauq.util.Formatos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GeneradorReportes {

    public static final int DIAS_ALERTA_VENCIMIENTO = 30;
    private static final String COMBO = "Combo";
    private static final String PRODUCTO = "Producto";

    private final ServicioCompras compras;
    private final ServicioCartelera cartelera;
    private final ServicioAutenticacion autenticacion;
    private final ServicioCancelaciones cancelaciones;
    private final EstadisticasOperacion estadisticas;
    private final Reloj reloj;

    public GeneradorReportes(ServicioCompras compras, ServicioCartelera cartelera, ServicioAutenticacion autenticacion,
                             ServicioCancelaciones cancelaciones, EstadisticasOperacion estadisticas, Reloj reloj) {
        this.compras = compras;
        this.cartelera = cartelera;
        this.autenticacion = autenticacion;
        this.cancelaciones = cancelaciones;
        this.estadisticas = estadisticas;
        this.reloj = reloj;
    }

    public Reporte generar(TipoReporte tipo) {
        switch (tipo) {
            case VENTAS_POR_PELICULA:
                return ventasPorPelicula();
            case OCUPACION_FUNCIONES:
                return ocupacionFunciones();
            case CONFITERIA:
                return confiteria();
            case PROGRAMA_PUNTOS:
                return programaPuntos();
            case CANCELACIONES:
                return cancelacionesYReembolsos();
            default:
                return resumenGeneral();
        }
    }

    public EstadisticasOperacion getEstadisticas() {
        return estadisticas;
    }

    private Reporte resumenGeneral() {
        Reporte reporte = new Reporte(TipoReporte.RESUMEN_GENERAL, reloj.ahora(),
                "Indicadores acumulados de la operación del cine, calculados a partir de los eventos registrados.");
        SeccionReporte indicadores = reporte.nuevaSeccion("Indicadores de operación", "Indicador", "Valor");
        indicadores.agregarFila("Compras confirmadas", String.valueOf(estadisticas.getComprasConfirmadas()));
        indicadores.agregarFila("Entradas vendidas", String.valueOf(estadisticas.getEntradasVendidas()));
        indicadores.agregarFila("Valor vendido", Formatos.pesos(estadisticas.getValorVendido()));
        indicadores.agregarFila("Compras canceladas", String.valueOf(estadisticas.getComprasCanceladas()));
        indicadores.agregarFila("Valor reembolsado", Formatos.pesos(estadisticas.getValorReembolsado()));
        indicadores.agregarFila("Ingreso neto", Formatos.pesos(estadisticas.getIngresoNeto()));
        indicadores.agregarFila("Ticket promedio", Formatos.pesos(estadisticas.getTicketPromedio()));
        indicadores.agregarFila("Tasa de cancelación", porcentaje(estadisticas.getTasaCancelacion()));
        indicadores.agregarFila("Funciones canceladas por el cine", String.valueOf(estadisticas.getFuncionesCanceladas()));
        indicadores.agregarFila("Recargas realizadas", String.valueOf(estadisticas.getRecargas()));
        indicadores.agregarFila("Valor recargado", Formatos.pesos(estadisticas.getValorRecargado()));
        indicadores.agregarFila("Clientes registrados", String.valueOf(autenticacion.listarClientes().size()));
        indicadores.agregarFila("Películas en cartelera", String.valueOf(cartelera.peliculasEnCartelera().size()));
        indicadores.agregarFila("Funciones con venta abierta", String.valueOf(cartelera.funcionesDisponibles().size()));
        indicadores.agregarFila("Ocupación promedio", porcentaje(ocupacionPromedio()));

        SeccionReporte dias = reporte.nuevaSeccion("Ventas por día", "Fecha", "Ventas", "Reembolsos", "Neto");
        Map<LocalDate, Long> ventas = ventasPorDia(14);
        Map<LocalDate, Long> reembolsos = reembolsosPorDia(14);
        for (Map.Entry<LocalDate, Long> dia : ventas.entrySet()) {
            long reembolsado = reembolsos.get(dia.getKey());
            dias.agregarFila(dia.getKey().format(Formatos.FECHA), Formatos.pesos(dia.getValue()),
                    Formatos.pesos(reembolsado), Formatos.pesos(dia.getValue() - reembolsado));
        }

        SeccionReporte reglas = reporte.nuevaSeccion("Reglas vigentes");
        reglas.agregarNota("Puntos: " + compras.getDescripcionReglaPuntos() + ", con vigencia de "
                + AcumulacionPuntos.MESES_VIGENCIA + " meses desde la fecha en que se obtienen.");
        reglas.agregarNota(cancelaciones.getPoliticaCliente().getDescripcion());
        reglas.agregarNota(cancelaciones.getPoliticaCine().getDescripcion());
        return reporte;
    }

    private Reporte ventasPorPelicula() {
        Reporte reporte = new Reporte(TipoReporte.VENTAS_POR_PELICULA, reloj.ahora(),
                "Solo se cuentan las compras pagadas; las compras canceladas aparecen en el reporte de cancelaciones.");
        SeccionReporte tabla = reporte.nuevaSeccion("Ventas por película", "Película", "Funciones", "Entradas",
                "Ingreso entradas", "Ingreso total", "Participación");
        Map<String, Long> ingresos = ingresosPorPelicula();
        long totalGeneral = 0;
        for (long valor : ingresos.values()) {
            totalGeneral = totalGeneral + valor;
        }
        int totalEntradas = 0;
        for (Map.Entry<String, Long> fila : ingresos.entrySet()) {
            Pelicula pelicula = buscarPeliculaPorTitulo(fila.getKey());
            int funciones = 0;
            int entradas = 0;
            long ingresoEntradas = 0;
            if (pelicula != null) {
                funciones = cartelera.funcionesDePelicula(pelicula.getCodigo()).size();
            }
            for (Compra compra : comprasPagadas()) {
                if (compra.getFuncion().getPelicula().getTitulo().equals(fila.getKey())) {
                    entradas = entradas + compra.getEntradas().size();
                    ingresoEntradas = ingresoEntradas + compra.getSubtotalEntradas();
                }
            }
            totalEntradas = totalEntradas + entradas;
            tabla.agregarFila(fila.getKey(), String.valueOf(funciones), String.valueOf(entradas),
                    Formatos.pesos(ingresoEntradas), Formatos.pesos(fila.getValue()),
                    porcentaje(participacion(fila.getValue(), totalGeneral)));
        }
        tabla.agregarNota("Total: " + totalEntradas + " entradas y " + Formatos.pesos(totalGeneral)
                + " en compras pagadas (incluye confitería y descuentos).");
        return reporte;
    }

    private Reporte ocupacionFunciones() {
        Reporte reporte = new Reporte(TipoReporte.OCUPACION_FUNCIONES, reloj.ahora(),
                "Ocupación de cada función según los asientos vendidos frente a la capacidad de la sala.");
        SeccionReporte tabla = reporte.nuevaSeccion("Funciones programadas", "Código", "Película", "Sala", "Fecha",
                "Vendidos", "Capacidad", "Ocupación");
        List<Funcion> canceladas = new ArrayList<>();
        for (Funcion funcion : cartelera.listarFunciones()) {
            if (funcion.getEstado() == EstadoFuncion.CANCELADA) {
                canceladas.add(funcion);
                continue;
            }
            tabla.agregarFila(funcion.getCodigo(), funcion.getPelicula().getTitulo(), funcion.getSala().getNombre(),
                    funcion.getFechaHora().format(Formatos.FECHA_HORA),
                    String.valueOf(funcion.contarAsientos(EstadoAsiento.VENDIDO)),
                    String.valueOf(funcion.getSala().getCapacidad()), porcentaje(funcion.getPorcentajeOcupacion()));
        }
        tabla.agregarNota("Ocupación promedio de las funciones activas: " + porcentaje(ocupacionPromedio()));
        if (!canceladas.isEmpty()) {
            SeccionReporte seccion = reporte.nuevaSeccion("Funciones canceladas por el cine", "Código", "Película",
                    "Sala", "Fecha");
            for (Funcion funcion : canceladas) {
                seccion.agregarFila(funcion.getCodigo(), funcion.getPelicula().getTitulo(), funcion.getSala().getNombre(),
                        funcion.getFechaHora().format(Formatos.FECHA_HORA));
            }
        }
        return reporte;
    }

    private Reporte confiteria() {
        Reporte reporte = new Reporte(TipoReporte.CONFITERIA, reloj.ahora(),
                "Productos y combos vendidos en compras pagadas, separando lo pagado con dinero y lo redimido con puntos.");
        SeccionReporte tabla = reporte.nuevaSeccion("Ventas por producto o combo", "Producto o combo", "Tipo",
                "Unidades pagadas", "Unidades con puntos", "Ingresos");
        Map<String, long[]> acumulado = new LinkedHashMap<>();
        Map<String, String> tipos = new LinkedHashMap<>();
        for (Compra compra : comprasPagadas()) {
            for (LineaConfiteria linea : compra.getLineasConfiteria()) {
                String nombre = linea.getItem().getNombre();
                long[] valores = acumulado.get(nombre);
                if (valores == null) {
                    valores = new long[3];
                    acumulado.put(nombre, valores);
                    tipos.put(nombre, linea.getItem() instanceof Combo ? COMBO : PRODUCTO);
                }
                if (linea.isRedimidaConPuntos()) {
                    valores[1] = valores[1] + linea.getCantidad();
                } else {
                    valores[0] = valores[0] + linea.getCantidad();
                }
                valores[2] = valores[2] + linea.getSubtotal();
            }
        }
        long totalIngresos = 0;
        long totalUnidades = 0;
        for (String nombre : ordenarPorValor(acumulado, 2)) {
            long[] valores = acumulado.get(nombre);
            totalIngresos = totalIngresos + valores[2];
            totalUnidades = totalUnidades + valores[0] + valores[1];
            tabla.agregarFila(nombre, tipos.get(nombre), String.valueOf(valores[0]), String.valueOf(valores[1]),
                    Formatos.pesos(valores[2]));
        }
        tabla.agregarNota("Total: " + totalUnidades + " unidades y " + Formatos.pesos(totalIngresos)
                + " antes de descuentos por promoción.");
        return reporte;
    }

    private Reporte programaPuntos() {
        LocalDate hoy = reloj.ahora().toLocalDate();
        Reporte reporte = new Reporte(TipoReporte.PROGRAMA_PUNTOS, reloj.ahora(),
                "Estado de los puntos de cada cliente al " + hoy.format(Formatos.FECHA) + ".");
        SeccionReporte tabla = reporte.nuevaSeccion("Puntos por cliente", "Cliente", "Acumulados", "Redimidos",
                "Vencidos", "Por vencer (" + DIAS_ALERTA_VENCIMIENTO + " días)", "Disponibles");
        int totalDisponibles = 0;
        int totalVencidos = 0;
        for (Cliente cliente : autenticacion.listarClientes()) {
            CuentaPuntos cuenta = cliente.getCuentaPuntos();
            int disponibles = cuenta.getPuntosDisponibles(hoy);
            int vencidos = cuenta.getPuntosVencidos(hoy);
            totalDisponibles = totalDisponibles + disponibles;
            totalVencidos = totalVencidos + vencidos;
            tabla.agregarFila(cliente.getNombre(), String.valueOf(cuenta.getTotalAcumulado()),
                    String.valueOf(cuenta.getTotalRedimido()), String.valueOf(vencidos),
                    String.valueOf(cuenta.getPuntosPorVencer(hoy, DIAS_ALERTA_VENCIMIENTO)), String.valueOf(disponibles));
        }
        tabla.agregarNota("Puntos disponibles en total: " + totalDisponibles + ". Puntos vencidos sin usar: " + totalVencidos + ".");
        SeccionReporte reglas = reporte.nuevaSeccion("Reglas del programa");
        reglas.agregarNota("Acumulación: " + compras.getDescripcionReglaPuntos() + ".");
        reglas.agregarNota("Vigencia: cada lote de puntos vence " + AcumulacionPuntos.MESES_VIGENCIA
                + " meses después de obtenerse.");
        reglas.agregarNota("Redención: se consumen primero los lotes que vencen antes. Entrada general "
                + TipoAsiento.GENERAL.getPuntosRedencion() + " pts, preferencial "
                + TipoAsiento.PREFERENCIAL.getPuntosRedencion() + " pts; cada producto tiene su costo en puntos.");
        reglas.agregarNota("Cancelación: se anulan los puntos ganados en la compra; si el cine cancela la función, "
                + "se devuelven los puntos redimidos.");
        return reporte;
    }

    private Reporte cancelacionesYReembolsos() {
        Reporte reporte = new Reporte(TipoReporte.CANCELACIONES, reloj.ahora(),
                "Compras canceladas por el cliente o por cancelación de la función, con el reembolso aplicado.");
        SeccionReporte tabla = reporte.nuevaSeccion("Compras canceladas", "Compra", "Cliente", "Película",
                "Fecha reembolso", "Porcentaje", "Pagado", "Reembolsado", "Motivo");
        long totalPagado = 0;
        long totalReembolsado = 0;
        for (Compra compra : compras.listarCompras()) {
            if (compra.getEstado() != EstadoCompra.CANCELADA || compra.getReembolso() == null) {
                continue;
            }
            totalPagado = totalPagado + compra.getTotal();
            totalReembolsado = totalReembolsado + compra.getReembolso().getValor();
            tabla.agregarFila(compra.getCodigo(), compra.getCliente().getNombre(),
                    compra.getFuncion().getPelicula().getTitulo(),
                    compra.getReembolso().getFecha().format(Formatos.FECHA_HORA),
                    compra.getReembolso().getPorcentaje() + " %", Formatos.pesos(compra.getTotal()),
                    Formatos.pesos(compra.getReembolso().getValor()), compra.getReembolso().getMotivo());
        }
        tabla.agregarNota("Total pagado en compras canceladas: " + Formatos.pesos(totalPagado)
                + ". Total reembolsado: " + Formatos.pesos(totalReembolsado)
                + ". Retenido por el cine: " + Formatos.pesos(totalPagado - totalReembolsado) + ".");
        SeccionReporte politicas = reporte.nuevaSeccion("Políticas aplicadas");
        politicas.agregarNota(cancelaciones.getPoliticaCliente().getDescripcion());
        politicas.agregarNota(cancelaciones.getPoliticaCine().getDescripcion());
        return reporte;
    }

    public Map<String, Long> ingresosPorPelicula() {
        Map<String, Long> ingresos = new LinkedHashMap<>();
        for (Compra compra : comprasPagadas()) {
            String titulo = compra.getFuncion().getPelicula().getTitulo();
            Long actual = ingresos.get(titulo);
            if (actual == null) {
                actual = 0L;
            }
            ingresos.put(titulo, actual + compra.getTotal());
        }
        Map<String, Long> ordenado = new LinkedHashMap<>();
        for (String titulo : ordenarLargos(ingresos)) {
            ordenado.put(titulo, ingresos.get(titulo));
        }
        return ordenado;
    }

    public Map<String, Integer> entradasPorTipo() {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        conteo.put("General", 0);
        conteo.put("Preferencial", 0);
        conteo.put("Cortesía (puntos)", 0);
        for (Compra compra : comprasPagadas()) {
            for (Entrada entrada : compra.getEntradas()) {
                String tipo;
                if (entrada.isCortesia()) {
                    tipo = "Cortesía (puntos)";
                } else if (entrada.getAsiento().getTipo() == TipoAsiento.PREFERENCIAL) {
                    tipo = "Preferencial";
                } else {
                    tipo = "General";
                }
                conteo.put(tipo, conteo.get(tipo) + 1);
            }
        }
        return conteo;
    }

    public Map<String, Long> unidadesConfiteria() {
        Map<String, Long> unidades = new LinkedHashMap<>();
        for (Compra compra : comprasPagadas()) {
            for (LineaConfiteria linea : compra.getLineasConfiteria()) {
                String nombre = linea.getItem().getNombre();
                Long actual = unidades.get(nombre);
                if (actual == null) {
                    actual = 0L;
                }
                unidades.put(nombre, actual + linea.getCantidad());
            }
        }
        Map<String, Long> ordenado = new LinkedHashMap<>();
        for (String nombre : ordenarLargos(unidades)) {
            ordenado.put(nombre, unidades.get(nombre));
        }
        return ordenado;
    }

    public Map<String, Double> ocupacionPorSala() {
        Map<String, double[]> acumulado = new LinkedHashMap<>();
        LocalDateTime ahora = reloj.ahora();
        for (Funcion funcion : cartelera.listarFunciones()) {
            if (funcion.getEstado() == EstadoFuncion.CANCELADA || funcion.getFechaHora().isAfter(ahora)) {
                continue;
            }
            String sala = funcion.getSala().getNombre();
            double[] valores = acumulado.get(sala);
            if (valores == null) {
                valores = new double[2];
                acumulado.put(sala, valores);
            }
            valores[0] = valores[0] + funcion.getPorcentajeOcupacion();
            valores[1] = valores[1] + 1;
        }
        Map<String, Double> promedio = new LinkedHashMap<>();
        for (Map.Entry<String, double[]> sala : acumulado.entrySet()) {
            promedio.put(sala.getKey(), sala.getValue()[0] / sala.getValue()[1]);
        }
        return promedio;
    }

    public Map<LocalDate, Long> ventasPorDia(int dias) {
        return completarDias(estadisticas.getVentasPorDia(), dias);
    }

    public Map<LocalDate, Long> reembolsosPorDia(int dias) {
        return completarDias(estadisticas.getReembolsosPorDia(), dias);
    }

    private Map<LocalDate, Long> completarDias(Map<LocalDate, Long> valores, int dias) {
        LocalDate hoy = reloj.ahora().toLocalDate();
        Map<LocalDate, Long> resultado = new LinkedHashMap<>();
        for (int i = dias - 1; i >= 0; i--) {
            LocalDate dia = hoy.minusDays(i);
            Long valor = valores.get(dia);
            if (valor == null) {
                valor = 0L;
            }
            resultado.put(dia, valor);
        }
        return resultado;
    }

    private double ocupacionPromedio() {
        double suma = 0;
        int cantidad = 0;
        for (Funcion funcion : cartelera.listarFunciones()) {
            if (funcion.getEstado() != EstadoFuncion.CANCELADA) {
                suma = suma + funcion.getPorcentajeOcupacion();
                cantidad++;
            }
        }
        if (cantidad == 0) {
            return 0;
        }
        return suma / cantidad;
    }

    private List<Compra> comprasPagadas() {
        List<Compra> pagadas = new ArrayList<>();
        for (Compra compra : compras.listarCompras()) {
            if (compra.getEstado() == EstadoCompra.PAGADA) {
                pagadas.add(compra);
            }
        }
        return pagadas;
    }

    private Pelicula buscarPeliculaPorTitulo(String titulo) {
        for (Pelicula pelicula : cartelera.listarPeliculas()) {
            if (pelicula.getTitulo().equals(titulo)) {
                return pelicula;
            }
        }
        return null;
    }

    private static List<String> ordenarLargos(Map<String, Long> valores) {
        List<String> claves = new ArrayList<>();
        for (String clave : valores.keySet()) {
            int posicion = 0;
            while (posicion < claves.size() && valores.get(claves.get(posicion)) >= valores.get(clave)) {
                posicion++;
            }
            claves.add(posicion, clave);
        }
        return claves;
    }

    private static List<String> ordenarPorValor(Map<String, long[]> valores, int indice) {
        List<String> claves = new ArrayList<>();
        for (String clave : valores.keySet()) {
            int posicion = 0;
            while (posicion < claves.size() && valores.get(claves.get(posicion))[indice] >= valores.get(clave)[indice]) {
                posicion++;
            }
            claves.add(posicion, clave);
        }
        return claves;
    }

    private static double participacion(long valor, long total) {
        if (total == 0) {
            return 0;
        }
        return 100.0 * valor / total;
    }

    private static String porcentaje(double valor) {
        return String.format(Locale.ROOT, "%.1f %%", valor);
    }
}
