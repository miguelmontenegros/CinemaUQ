package co.edu.uniquindio.cinemauq.controlador.compra;

import co.edu.uniquindio.cinemauq.excepciones.CompraInvalidaException;
import co.edu.uniquindio.cinemauq.fachada.CinemaUQFachada;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.Promocion;
import co.edu.uniquindio.cinemauq.modelo.enums.AdicionalEntrada;
import co.edu.uniquindio.cinemauq.util.Formatos;

import java.util.ArrayList;
import java.util.List;

public class PantallaCompraMediador implements MediadorCompra {

    private final CinemaUQFachada fachada;
    private final SelectorFuncion selectorFuncion;
    private final MapaAsientos mapaAsientos;
    private final OpcionesEntrada opciones;
    private final CarritoConfiteria carrito;
    private final SelectorPromocion selectorPromocion;
    private final ResumenCompra resumen;
    private final BotonPagar botonPagar;

    public PantallaCompraMediador(CinemaUQFachada fachada, SelectorFuncion selectorFuncion, MapaAsientos mapaAsientos,
                                  OpcionesEntrada opciones, CarritoConfiteria carrito,
                                  SelectorPromocion selectorPromocion, ResumenCompra resumen, BotonPagar botonPagar) {
        this.fachada = fachada;
        this.selectorFuncion = selectorFuncion;
        this.mapaAsientos = mapaAsientos;
        this.opciones = opciones;
        this.carrito = carrito;
        this.selectorPromocion = selectorPromocion;
        this.resumen = resumen;
        this.botonPagar = botonPagar;
        selectorFuncion.setMediador(this);
        mapaAsientos.setMediador(this);
        opciones.setMediador(this);
        carrito.setMediador(this);
        selectorPromocion.setMediador(this);
        resumen.setMediador(this);
        botonPagar.setMediador(this);
        opciones.ajustarASala(null);
    }

    @Override
    public void notificar(ComponenteCompra emisor, String evento) {
        switch (evento) {
            case FUNCION_ELEGIDA:
                Funcion funcion = selectorFuncion.getFuncion();
                mapaAsientos.mostrar(funcion);
                if (funcion == null) {
                    opciones.ajustarASala(null);
                } else {
                    opciones.ajustarASala(funcion.getSala().getTipo());
                }
                actualizarPantalla();
                break;
            case ASIENTOS_CAMBIARON:
            case OPCIONES_CAMBIARON:
            case CARRITO_CAMBIO:
            case PROMOCION_CAMBIO:
                actualizarPantalla();
                break;
            default:
                break;
        }
    }

    public void refrescarDatos() {
        carrito.cargarProductos(fachada.consultarConfiteria());
        selectorPromocion.cargar(fachada.consultarPromociones());
        selectorFuncion.cargar(fachada.consultarCartelera());
        mapaAsientos.mostrar(selectorFuncion.getFuncion());
        actualizarPantalla();
    }

    public void reiniciar() {
        mapaAsientos.limpiarSeleccion();
        opciones.limpiar();
        carrito.vaciar();
        selectorPromocion.limpiar();
        actualizarPantalla();
    }

    public Compra armarCompra() {
        Funcion funcion = selectorFuncion.getFuncion();
        if (funcion == null) {
            throw new CompraInvalidaException("Primero seleccione una función de la cartelera");
        }
        return fachada.prepararCompra(funcion, mapaAsientos.getSeleccionados(), opciones.isConPuntos(),
                carrito.getLineas(), selectorPromocion.getPromocion(), opciones.getAdicionales());
    }

    private void actualizarPantalla() {
        List<String> lineas = new ArrayList<>();
        Funcion funcion = selectorFuncion.getFuncion();
        int asientos = mapaAsientos.getSeleccionados().size();
        int puntosRequeridos = carrito.puntosParaRedimir();
        if (opciones.isConPuntos()) {
            puntosRequeridos = puntosRequeridos + mapaAsientos.puntosParaRedimir();
        }
        int puntosDisponibles = fachada.consultarPuntosDisponibles();

        if (funcion == null) {
            lineas.add("Función: sin seleccionar");
        } else {
            lineas.add("Función: " + funcion.getPelicula().getTitulo() + ", "
                    + funcion.getFechaHora().format(Formatos.FECHA_HORA));
        }
        String detalleAsientos = "Entradas: " + asientos;
        if (asientos > 0) {
            detalleAsientos = detalleAsientos + " (" + mapaAsientos.contarPreferenciales() + " preferencial(es))";
            if (opciones.isConPuntos()) {
                detalleAsientos = detalleAsientos + " con puntos";
            }
        }
        lineas.add(detalleAsientos);
        for (AdicionalEntrada adicional : opciones.getAdicionales()) {
            lineas.add("Adicional por entrada: " + adicional.getNombre());
        }
        lineas.add("Confitería: " + carrito.contarUnidades() + " unidad(es), " + Formatos.pesos(carrito.subtotalEnDinero())
                + " en dinero");
        Promocion promocion = selectorPromocion.getPromocion();
        if (promocion == null) {
            lineas.add("Promoción: ninguna");
        } else {
            lineas.add("Promoción: " + promocion);
        }
        lineas.add("Puntos a usar: " + puntosRequeridos + " de " + puntosDisponibles + " disponibles");

        String impedimento = buscarImpedimento(funcion, asientos, puntosRequeridos, puntosDisponibles);
        botonPagar.setHabilitado(impedimento == null);
        if (impedimento == null) {
            resumen.mostrar(lineas, "Listo para revisar el valor final y pagar.");
        } else {
            resumen.mostrar(lineas, impedimento);
        }
    }

    private String buscarImpedimento(Funcion funcion, int asientos, int puntosRequeridos, int puntosDisponibles) {
        if (funcion == null) {
            return "Seleccione una función para empezar.";
        }
        if (asientos == 0 && carrito.estaVacio()) {
            return "Seleccione asientos o agregue productos de confitería.";
        }
        if (asientos == 0 && !opciones.getAdicionales().isEmpty()) {
            return "Los adicionales solo se aplican a entradas: seleccione al menos un asiento.";
        }
        if (asientos == 0 && opciones.isConPuntos()) {
            return "Marcó entradas con puntos pero no hay asientos seleccionados.";
        }
        if (puntosRequeridos > puntosDisponibles) {
            return "Puntos insuficientes: faltan " + (puntosRequeridos - puntosDisponibles) + " puntos.";
        }
        if (!fachada.consultarTarjeta().isActiva()) {
            return "Su tarjeta está bloqueada. Comuníquese con el administrador.";
        }
        return null;
    }
}
