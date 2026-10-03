package co.edu.uniquindio.cinemauq.app;

import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Sala;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.Promocion;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;
import co.edu.uniquindio.cinemauq.repositorio.RepositorioMemoria;
import co.edu.uniquindio.cinemauq.servicio.Reloj;
import co.edu.uniquindio.cinemauq.servicio.ServicioAutenticacion;
import co.edu.uniquindio.cinemauq.servicio.ServicioCancelaciones;
import co.edu.uniquindio.cinemauq.servicio.ServicioCartelera;
import co.edu.uniquindio.cinemauq.servicio.ServicioCompras;
import co.edu.uniquindio.cinemauq.servicio.ServicioConfiteria;
import co.edu.uniquindio.cinemauq.servicio.ServicioTarjetas;
import co.edu.uniquindio.cinemauq.servicio.politicas.AcumulacionPorValorPagado;
import co.edu.uniquindio.cinemauq.servicio.politicas.ReembolsoFuncionCancelada;
import co.edu.uniquindio.cinemauq.servicio.politicas.ReembolsoPorAnticipacion;

public class ContextoAplicacion {
    private final Reloj reloj;
    private final ServicioAutenticacion autenticacion;
    private final ServicioCartelera cartelera;
    private final ServicioConfiteria confiteria;
    private final ServicioCompras compras;
    private final ServicioTarjetas tarjetas;
    private final ServicioCancelaciones cancelaciones;

    public ContextoAplicacion(Reloj reloj) {
        this.reloj = reloj;

        Repositorio<Usuario> repoUsuarios = new RepositorioMemoria<>();
        Repositorio<Pelicula> repoPeliculas = new RepositorioMemoria<>();
        Repositorio<Sala> repoSalas = new RepositorioMemoria<>();
        Repositorio<Funcion> repoFunciones = new RepositorioMemoria<>();
        Repositorio<ItemConfiteria> repoItems = new RepositorioMemoria<>();
        Repositorio<Promocion> repoPromociones = new RepositorioMemoria<>();
        Repositorio<Compra> repoCompras = new RepositorioMemoria<>();
        Repositorio<SolicitudRecarga> repoSolicitudes = new RepositorioMemoria<>();

        this.autenticacion = new ServicioAutenticacion(repoUsuarios);
        this.cartelera = new ServicioCartelera(repoPeliculas, repoSalas, repoFunciones, reloj);
        this.confiteria = new ServicioConfiteria(repoItems, repoPromociones, reloj);
        this.compras = new ServicioCompras(repoCompras, new AcumulacionPorValorPagado(), reloj);
        this.tarjetas = new ServicioTarjetas(repoSolicitudes, reloj);
        this.cancelaciones = new ServicioCancelaciones(repoCompras, new ReembolsoPorAnticipacion(),
                new ReembolsoFuncionCancelada(), reloj);
    }

    public Reloj getReloj() {
        return reloj;
    }

    public ServicioAutenticacion getAutenticacion() {
        return autenticacion;
    }

    public ServicioCartelera getCartelera() {
        return cartelera;
    }

    public ServicioConfiteria getConfiteria() {
        return confiteria;
    }

    public ServicioCompras getCompras() {
        return compras;
    }

    public ServicioTarjetas getTarjetas() {
        return tarjetas;
    }

    public ServicioCancelaciones getCancelaciones() {
        return cancelaciones;
    }
}