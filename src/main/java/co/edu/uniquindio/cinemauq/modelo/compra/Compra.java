package co.edu.uniquindio.cinemauq.modelo.compra;

import co.edu.uniquindio.cinemauq.excepciones.CancelacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.excepciones.CompraInvalidaException;
import co.edu.uniquindio.cinemauq.excepciones.ReembolsoDuplicadoException;
import co.edu.uniquindio.cinemauq.modelo.GeneradorCodigos;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Asiento;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.emision.CatalogoEmisores;
import co.edu.uniquindio.cinemauq.modelo.compra.emision.EmisorEntradas;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.contratos.Identificable;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoCompra;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoCodigo;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Compra implements Identificable {

    public static final int MAX_ENTRADAS_POR_COMPRA = 10;

    private final String codigo;
    private final Cliente cliente;
    private final Funcion funcion;
    private final LocalDateTime fecha;
    private final List<Entrada> entradas;
    private final List<LineaConfiteria> lineasConfiteria;
    private final Promocion promocion;
    private final long subtotalEntradas;
    private final long subtotalConfiteria;
    private final long descuento;

    private EstadoCompra estado = EstadoCompra.PENDIENTE;
    private TarjetaVirtual tarjetaPago;
    private int puntosGanados;
    private Reembolso reembolso;

    private Compra(Builder b, List<Entrada> entradas) {
        this.codigo = GeneradorCodigos.getInstancia().siguiente(TipoCodigo.COMPRA);
        this.cliente = b.cliente;
        this.funcion = b.funcion;
        this.fecha = b.fecha;
        this.entradas = new ArrayList<>(entradas);
        this.lineasConfiteria = new ArrayList<>(b.lineas);
        this.promocion = b.promocion;

        long sumaEntradas = 0;
        for (Entrada entrada : this.entradas) {
            sumaEntradas = sumaEntradas + entrada.getPrecio();
        }
        long sumaConfiteria = 0;
        for (LineaConfiteria linea : this.lineasConfiteria) {
            sumaConfiteria = sumaConfiteria + linea.getSubtotal();
        }
        this.subtotalEntradas = sumaEntradas;
        this.subtotalConfiteria = sumaConfiteria;
        if (promocion == null) {
            this.descuento = 0;
        } else {
            this.descuento = promocion.calcularDescuento(subtotalEntradas, subtotalConfiteria);
        }
    }


    public void confirmarPago(TarjetaVirtual tarjeta, int puntosGanados) {
        if (estado != EstadoCompra.PENDIENTE) {
            throw new CompraInvalidaException("La compra " + codigo + " ya fue procesada");
        }
        this.tarjetaPago = tarjeta;
        this.puntosGanados = puntosGanados;
        this.estado = EstadoCompra.PAGADA;
    }

    public void registrarCancelacion(Reembolso reembolso) {
        if (this.reembolso != null) {
            throw new ReembolsoDuplicadoException("La compra " + codigo + " ya fue reembolsada (" + this.reembolso.getCodigo() + ")");
        }
        if (estado != EstadoCompra.PAGADA) {
            throw new CancelacionNoPermitidaException("Solo se pueden cancelar compras pagadas");
        }
        this.reembolso = reembolso;
        this.estado = EstadoCompra.CANCELADA;
    }


    public long getSubtotalEntradas() {
        return subtotalEntradas;
    }

    public long getSubtotalConfiteria() {
        return subtotalConfiteria;
    }

    public long getDescuento() {
        return descuento;
    }

    public long getTotal() {
        return subtotalEntradas + subtotalConfiteria - descuento;
    }

    public int getPuntosRedimidos() {
        int puntos = 0;
        for (Entrada entrada : entradas) {
            if (entrada.isCortesia()) {
                puntos = puntos + entrada.getCostoPuntos();
            }
        }
        for (LineaConfiteria linea : lineasConfiteria) {
            puntos = puntos + linea.getPuntos();
        }
        return puntos;
    }


    @Override
    public String getIdentificador() {
        return codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public List<Entrada> getEntradas() {
        return Collections.unmodifiableList(entradas);
    }

    public List<LineaConfiteria> getLineasConfiteria() {
        return Collections.unmodifiableList(lineasConfiteria);
    }

    public Promocion getPromocion() {
        return promocion;
    }

    public EstadoCompra getEstado() {
        return estado;
    }

    public TarjetaVirtual getTarjetaPago() {
        return tarjetaPago;
    }

    public int getPuntosGanados() {
        return puntosGanados;
    }

    public Reembolso getReembolso() {
        return reembolso;
    }

    @Override
    public String toString() {
        return codigo + " · " + funcion.getPelicula().getTitulo() + " · " + entradas.size() + " entrada(s) · " + estado;
    }


    public static class Builder {

        private Cliente cliente;
        private Funcion funcion;
        private LocalDateTime fecha;
        private final List<String> asientosPagados = new ArrayList<>();
        private final List<String> asientosRedimidos = new ArrayList<>();
        private final List<LineaConfiteria> lineas = new ArrayList<>();
        private Promocion promocion;

        public Builder conCliente(Cliente cliente) {
            this.cliente = cliente;
            return this;
        }

        public Builder paraFuncion(Funcion funcion) {
            this.funcion = funcion;
            return this;
        }

        public Builder enFecha(LocalDateTime fecha) {
            this.fecha = fecha;
            return this;
        }

        public Builder conAsiento(String codigoAsiento) {
            asientosPagados.add(codigoAsiento.toUpperCase());
            return this;
        }

        public Builder conAsientoRedimido(String codigoAsiento) {
            asientosRedimidos.add(codigoAsiento.toUpperCase());
            return this;
        }

        public Builder conProducto(ItemConfiteria item, int cantidad) {
            lineas.add(new LineaConfiteria(item, cantidad, false));
            return this;
        }

        public Builder conProductoRedimido(ItemConfiteria item, int cantidad) {
            lineas.add(new LineaConfiteria(item, cantidad, true));
            return this;
        }

        public Builder conPromocion(Promocion promocion) {
            this.promocion = promocion;
            return this;
        }

        public Compra build() {
            if (cliente == null) {
                throw new CompraInvalidaException("La compra requiere un cliente");
            }
            if (funcion == null) {
                throw new CompraInvalidaException("La compra requiere una función (RN-09)");
            }
            if (fecha == null) {
                throw new CompraInvalidaException("La compra requiere la fecha de la operación");
            }
            funcion.validarAdmiteVentas(fecha);

            int totalEntradas = asientosPagados.size() + asientosRedimidos.size();
            if (totalEntradas == 0 && lineas.isEmpty()) {
                throw new CompraInvalidaException("La compra debe tener al menos una entrada o un producto");
            }
            if (totalEntradas > MAX_ENTRADAS_POR_COMPRA) {
                throw new CompraInvalidaException("Máximo " + MAX_ENTRADAS_POR_COMPRA + " entradas por compra (RN-18)");
            }
            List<String> todosLosAsientos = new ArrayList<>();
            todosLosAsientos.addAll(asientosPagados);
            todosLosAsientos.addAll(asientosRedimidos);
            List<String> sinRepetir = new ArrayList<>();
            for (String codigo : todosLosAsientos) {
                if (sinRepetir.contains(codigo)) {
                    throw new CompraInvalidaException("Un asiento se seleccionó dos veces en la misma compra");
                }
                sinRepetir.add(codigo);
            }
            for (LineaConfiteria linea : lineas) {
                if (linea.getItem() == null || linea.getCantidad() <= 0) {
                    throw new CompraInvalidaException("Cada producto debe tener una cantidad mayor que cero");
                }
                if (!linea.getItem().isActivo()) {
                    throw new CompraInvalidaException(linea.getItem().getNombre() + " no está disponible");
                }
            }
            if (promocion != null && !promocion.estaVigente(fecha)) {
                throw new CompraInvalidaException("La promoción " + promocion.getNombre() + " no está vigente (RN-14)");
            }

            for (String codigo : sinRepetir) {
                funcion.getAsiento(codigo).validarDisponible();
            }

            List<Entrada> entradas = new ArrayList<>();
            for (String codigo : asientosPagados) {
                Asiento asiento = funcion.getAsiento(codigo).getAsiento();
                EmisorEntradas emisor = CatalogoEmisores.paraAsiento(asiento.getTipo());
                entradas.add(emisor.emitir(funcion, asiento));
            }
            for (String codigo : asientosRedimidos) {
                Asiento asiento = funcion.getAsiento(codigo).getAsiento();
                entradas.add(CatalogoEmisores.cortesia().emitir(funcion, asiento));
            }
            return new Compra(this, entradas);
        }
    }
}