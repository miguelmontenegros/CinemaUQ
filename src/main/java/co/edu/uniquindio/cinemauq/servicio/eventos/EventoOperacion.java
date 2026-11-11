package co.edu.uniquindio.cinemauq.servicio.eventos;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;

import java.time.LocalDateTime;

public class EventoOperacion {

    private final TipoEvento tipo;
    private final LocalDateTime momento;
    private final long valor;
    private final String descripcion;
    private final Cliente cliente;
    private final Compra compra;

    public EventoOperacion(TipoEvento tipo, LocalDateTime momento, long valor, String descripcion,
                           Cliente cliente, Compra compra) {
        this.tipo = tipo;
        this.momento = momento;
        this.valor = valor;
        this.descripcion = descripcion;
        this.cliente = cliente;
        this.compra = compra;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public LocalDateTime getMomento() {
        return momento;
    }

    public long getValor() {
        return valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Compra getCompra() {
        return compra;
    }
}
