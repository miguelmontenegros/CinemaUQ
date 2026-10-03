package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.Entrada;
import co.edu.uniquindio.cinemauq.modelo.puntos.CuentaPuntos;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;
import co.edu.uniquindio.cinemauq.servicio.politicas.ReglaAcumulacionPuntos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServicioCompras {

    private final Repositorio<Compra> compras;
    private final ReglaAcumulacionPuntos reglaPuntos;
    private final Reloj reloj;

    public ServicioCompras(Repositorio<Compra> compras, ReglaAcumulacionPuntos reglaPuntos, Reloj reloj) {
        this.compras = compras;
        this.reglaPuntos = reglaPuntos;
        this.reloj = reloj;
    }

    public Compra.Builder nuevaCompra(Cliente cliente) {
        return new Compra.Builder().conCliente(cliente).enFecha(reloj.ahora());
    }


    public Compra confirmarCompra(Compra compra) {
        LocalDateTime ahora = reloj.ahora();
        LocalDate hoy = ahora.toLocalDate();
        Cliente cliente = compra.getCliente();
        TarjetaVirtual tarjeta = cliente.getTarjeta();
        CuentaPuntos cuenta = cliente.getCuentaPuntos();
        Funcion funcion = compra.getFuncion();

        funcion.validarAdmiteVentas(ahora);
        if (compra.getTotal() > 0) {
            tarjeta.validarPago(compra.getTotal());
        }
        cuenta.validarRedencion(compra.getPuntosRedimidos(), hoy);
        for (Entrada entrada : compra.getEntradas()) {
            funcion.getAsiento(entrada.getAsiento().getCodigo()).validarDisponible();
        }

        for (Entrada entrada : compra.getEntradas()) {
            funcion.ocuparAsiento(entrada.getAsiento().getCodigo());
        }
        if (compra.getTotal() > 0) {
            tarjeta.debitar(compra.getTotal(), ahora, "Compra " + compra.getCodigo());
        }
        cuenta.redimir(compra.getPuntosRedimidos(), hoy, compra.getCodigo());
        int puntosGanados = reglaPuntos.calcularPuntos(compra);
        cuenta.acumular(puntosGanados, hoy, compra.getCodigo());

        compra.confirmarPago(tarjeta, puntosGanados);
        compras.guardar(compra);
        cliente.agregarCompra(compra);
        return compra;
    }

    public List<Compra> historial(Cliente cliente) {
        List<Compra> historial = new ArrayList<>();
        List<Compra> comprasCliente = cliente.getCompras();
        for (int i = comprasCliente.size() - 1; i >= 0; i--) {
            historial.add(comprasCliente.get(i));
        }
        return historial;
    }

    public List<Compra> comprasDeFuncion(Funcion funcion) {
        List<Compra> resultado = new ArrayList<>();
        for (Compra compra : compras.listar()) {
            if (compra.getFuncion() == funcion) {
                resultado.add(compra);
            }
        }
        return resultado;
    }

    public List<Compra> listarCompras() {
        return compras.listar();
    }

    public String getDescripcionReglaPuntos() {
        return reglaPuntos.getDescripcion();
    }
}
