package co.edu.uniquindio.cinemauq.modelo.tarjeta;

import co.edu.uniquindio.cinemauq.excepciones.SaldoInsuficienteException;
import co.edu.uniquindio.cinemauq.excepciones.TarjetaInactivaException;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoMovimiento;
import co.edu.uniquindio.cinemauq.util.Formatos;
import co.edu.uniquindio.cinemauq.util.Validaciones;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TarjetaVirtual {

    private final String numero;
    private long saldo;
    private boolean activa = true;
    private final List<Movimiento> movimientos = new ArrayList<>();

    public TarjetaVirtual(String numero) {
        this.numero = Validaciones.texto(numero, "número de tarjeta");
    }

    public void validarPago(long valor) {
        if (!activa) {
            throw new TarjetaInactivaException("La tarjeta " + numero + " está bloqueada");
        }
        if (saldo < valor) {
            throw new SaldoInsuficienteException("Saldo insuficiente: disponible " + Formatos.pesos(saldo)
                    + ", requerido " + Formatos.pesos(valor) + ". Solicite una recarga.");
        }
    }

    public void debitar(long valor, LocalDateTime fecha, String referencia) {
        Validaciones.positivo(valor, "valor del pago");
        validarPago(valor);
        saldo -= valor;
        movimientos.add(new Movimiento(TipoMovimiento.PAGO, valor, saldo, fecha, referencia));
    }

    public void recargar(long valor, LocalDateTime fecha, String referencia) {
        Validaciones.positivo(valor, "valor de la recarga");
        if (!activa) {
            throw new TarjetaInactivaException("No se puede recargar una tarjeta bloqueada");
        }
        saldo += valor;
        movimientos.add(new Movimiento(TipoMovimiento.RECARGA, valor, saldo, fecha, referencia));
    }

    public void acreditarReembolso(long valor, LocalDateTime fecha, String referencia) {
        if (valor <= 0) {
            return;
        }
        saldo += valor;
        movimientos.add(new Movimiento(TipoMovimiento.REEMBOLSO, valor, saldo, fecha, referencia));
    }

    public void bloquear() {
        activa = false;
    }

    public void activar() {
        activa = true;
    }

    public String getNumero() {
        return numero;
    }

    public long getSaldo() {
        return saldo;
    }

    public boolean isActiva() {
        return activa;
    }

    public List<Movimiento> getMovimientos() {
        return Collections.unmodifiableList(movimientos);
    }
}
