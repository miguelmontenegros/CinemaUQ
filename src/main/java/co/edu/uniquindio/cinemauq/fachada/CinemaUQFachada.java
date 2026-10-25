package co.edu.uniquindio.cinemauq.fachada;

import co.edu.uniquindio.cinemauq.app.ContextoAplicacion;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.LineaConfiteria;
import co.edu.uniquindio.cinemauq.modelo.compra.Promocion;
import co.edu.uniquindio.cinemauq.modelo.compra.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.enums.AdicionalEntrada;
import co.edu.uniquindio.cinemauq.modelo.puntos.AcumulacionPuntos;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.Movimiento;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;
import co.edu.uniquindio.cinemauq.servicio.SesionUsuario;
import co.edu.uniquindio.cinemauq.servicio.administracion.ProxyServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.administracion.ServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.administracion.ServicioAdministracionReal;

import java.time.LocalDate;
import java.util.List;

public class CinemaUQFachada {

    private final ContextoAplicacion contexto;
    private final SesionUsuario sesion;
    private final ProxyServicioAdministracion administracion;

    public CinemaUQFachada(ContextoAplicacion contexto) {
        this.contexto = contexto;
        this.sesion = new SesionUsuario();
        ServicioAdministracion real = new ServicioAdministracionReal(contexto.getAutenticacion(), contexto.getTarjetas(),
                contexto.getCartelera(), contexto.getCancelaciones(), sesion);
        this.administracion = new ProxyServicioAdministracion(real, sesion, contexto.getReloj());
    }

    public Usuario iniciarSesion(String correo, String contrasena) {
        Usuario usuario = contexto.getAutenticacion().iniciarSesion(correo, contrasena);
        sesion.iniciar(usuario);
        return usuario;
    }

    public Cliente registrarCliente(String cedula, String nombre, String correo, String contrasena) {
        return contexto.getAutenticacion().registrarCliente(cedula, nombre, correo, contrasena);
    }

    public void cerrarSesion() {
        sesion.cerrar();
    }

    public Usuario getUsuarioActual() {
        return sesion.getUsuarioActual();
    }

    public boolean esAdministrador() {
        return sesion.esAdministrador();
    }

    public List<Funcion> consultarCartelera() {
        return contexto.getCartelera().funcionesDisponibles();
    }

    public List<ItemConfiteria> consultarConfiteria() {
        return contexto.getConfiteria().itemsDisponibles();
    }

    public List<Promocion> consultarPromociones() {
        return contexto.getConfiteria().promocionesVigentes();
    }

    public AdicionalEntrada[] consultarAdicionales() {
        return AdicionalEntrada.values();
    }

    public Compra prepararCompra(Funcion funcion, List<String> asientos, boolean entradasConPuntos,
                                 List<LineaConfiteria> confiteria, Promocion promocion,
                                 List<AdicionalEntrada> adicionales) {
        Compra.Builder builder = contexto.getCompras().nuevaCompra(sesion.getCliente())
                .paraFuncion(funcion)
                .conPromocion(promocion);
        for (String asiento : asientos) {
            if (entradasConPuntos) {
                builder.conAsientoRedimido(asiento);
            } else {
                builder.conAsiento(asiento);
            }
        }
        for (LineaConfiteria linea : confiteria) {
            if (linea.isRedimidaConPuntos()) {
                builder.conProductoRedimido(linea.getItem(), linea.getCantidad());
            } else {
                builder.conProducto(linea.getItem(), linea.getCantidad());
            }
        }
        for (AdicionalEntrada adicional : adicionales) {
            builder.conAdicional(adicional);
        }
        return builder.build();
    }

    public Compra pagarCompra(Compra compra) {
        return contexto.getCompras().confirmarCompra(compra);
    }

    public List<Compra> consultarMisCompras() {
        return contexto.getCompras().historial(sesion.getCliente());
    }

    public Reembolso cancelarCompra(Compra compra) {
        return contexto.getCancelaciones().cancelarCompra(sesion.getCliente(), compra);
    }

    public TarjetaVirtual consultarTarjeta() {
        return sesion.getCliente().getTarjeta();
    }

    public List<Movimiento> consultarMovimientos() {
        return sesion.getCliente().getTarjeta().getMovimientos();
    }

    public SolicitudRecarga solicitarRecarga(long valor) {
        return contexto.getTarjetas().solicitarRecarga(sesion.getCliente(), valor);
    }

    public int consultarPuntosDisponibles() {
        LocalDate hoy = contexto.getReloj().ahora().toLocalDate();
        return sesion.getCliente().getCuentaPuntos().getPuntosDisponibles(hoy);
    }

    public List<AcumulacionPuntos> consultarLotesDePuntos() {
        return sesion.getCliente().getCuentaPuntos().getAcumulaciones();
    }

    public LocalDate fechaActual() {
        return contexto.getReloj().ahora().toLocalDate();
    }

    public ServicioAdministracion administracion() {
        return administracion;
    }

    public List<String> consultarBitacora() {
        return administracion.getBitacora();
    }
}