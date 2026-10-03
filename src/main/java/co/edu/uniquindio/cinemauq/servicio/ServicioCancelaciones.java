package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.excepciones.OperacionNoAutorizadaException;
import co.edu.uniquindio.cinemauq.excepciones.ReembolsoDuplicadoException;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.Entrada;
import co.edu.uniquindio.cinemauq.modelo.compra.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoCompra;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoFuncion;
import co.edu.uniquindio.cinemauq.modelo.puntos.CuentaPuntos;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Administrador;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;
import co.edu.uniquindio.cinemauq.servicio.politicas.PoliticaReembolso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServicioCancelaciones {

    private final Repositorio<Compra> compras;
    private final PoliticaReembolso politicaCliente;
    private final PoliticaReembolso politicaCine;
    private final Reloj reloj;

    public ServicioCancelaciones(Repositorio<Compra> compras, PoliticaReembolso politicaCliente,
                                 PoliticaReembolso politicaCine, Reloj reloj) {
        this.compras = compras;
        this.politicaCliente = politicaCliente;
        this.politicaCine = politicaCine;
        this.reloj = reloj;
    }

    public Reembolso cancelarCompra(Cliente solicitante, Compra compra) {
        if (compra.getCliente() != solicitante) {
            throw new OperacionNoAutorizadaException("Solo el titular puede cancelar la compra " + compra.getCodigo());
        }
        if (compra.getReembolso() != null) {
            throw new ReembolsoDuplicadoException("La compra " + compra.getCodigo() + " ya fue reembolsada");
        }
        return reembolsar(compra, politicaCliente);
    }

    public List<Reembolso> cancelarFuncion(Administrador admin, Funcion funcion) {
        if (admin == null) {
            throw new OperacionNoAutorizadaException("Solo un administrador puede cancelar funciones");
        }
        if (funcion.getEstado() == EstadoFuncion.CANCELADA) {
            throw new DatoInvalidoException("La función " + funcion.getCodigo() + " ya estaba cancelada");
        }
        funcion.cancelar();
        List<Reembolso> reembolsos = new ArrayList<>();
        for (Compra compra : compras.listar()) {
            if (compra.getFuncion() == funcion && compra.getEstado() == EstadoCompra.PAGADA) {
                reembolsos.add(reembolsar(compra, politicaCine));
            }
        }
        return reembolsos;
    }

    private Reembolso reembolsar(Compra compra, PoliticaReembolso politica) {
        LocalDateTime ahora = reloj.ahora();
        int porcentaje = politica.calcularPorcentaje(compra, ahora);
        long valor = Math.round(compra.getTotal() * porcentaje / 100.0);
        Reembolso reembolso = new Reembolso(porcentaje, valor, ahora, politica.getMotivo());

        compra.registrarCancelacion(reembolso);
        compra.getTarjetaPago().acreditarReembolso(valor, ahora, "Reembolso " + compra.getCodigo());
        for (Entrada entrada : compra.getEntradas()) {
            compra.getFuncion().liberarAsiento(entrada.getAsiento().getCodigo());
        }
        CuentaPuntos cuenta = compra.getCliente().getCuentaPuntos();
        cuenta.anularAcumulacion(compra.getCodigo());
        if (politica.devuelvePuntosRedimidos()) {
            cuenta.acumular(compra.getPuntosRedimidos(), ahora.toLocalDate(), "Devolución " + compra.getCodigo());
        }
        return reembolso;
    }
}
