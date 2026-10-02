package co.edu.uniquindio.cinemauq.app;

import co.edu.uniquindio.cinemauq.datos.DatosIniciales;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.modelo.GeneradorCodigos;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.Entrada;
import co.edu.uniquindio.cinemauq.modelo.compra.Promocion;
import co.edu.uniquindio.cinemauq.modelo.compra.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoAsiento;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoCodigo;
import co.edu.uniquindio.cinemauq.modelo.puntos.AcumulacionPuntos;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Administrador;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.servicio.RelojAjustable;
import co.edu.uniquindio.cinemauq.util.Formatos;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MainConsola {
    public static void main(String[] args) {
        RelojAjustable reloj = new RelojAjustable(LocalDateTime.of(2026, 10, 6, 10, 0));
        ContextoAplicacion ctx = new ContextoAplicacion(reloj);
        DatosIniciales.cargar(ctx);

        Administrador admin = (Administrador) ctx.getAutenticacion().iniciarSesion("admin@cinemauq.co", "admin123");
        Cliente laura = ctx.getAutenticacion().buscarCliente("laura@correo.com");
        Cliente andres = ctx.getAutenticacion().buscarCliente("andres@correo.com");
        Cliente sofia = ctx.getAutenticacion().buscarCliente("sofia@correo.com");
        LocalDate hoy = reloj.ahora().toLocalDate();

        titulo("1. SINGLETON · GeneradorCodigos");
        GeneradorCodigos g1 = GeneradorCodigos.getInstancia();
        GeneradorCodigos g2 = GeneradorCodigos.getInstancia();
        System.out.println("getInstancia() == getInstancia() -> " + (g1 == g2));
        System.out.println("Siguientes códigos de compra: " + g1.siguiente(TipoCodigo.COMPRA) + ", "
                + g2.siguiente(TipoCodigo.COMPRA));

        titulo("2. PROTOTYPE · Funcion.clone() con copia profunda del mapa de asientos");
        List<Funcion> funcionesDune = ctx.getCartelera().funcionesDePelicula("P001");
        Funcion plantilla = funcionesDune.get(0);
        Funcion clon1 = funcionesDune.get(1);
        Funcion clon2 = funcionesDune.get(2);
        System.out.println("Plantilla: " + plantilla.getCodigo() + " " + plantilla);
        System.out.println("Clon 1:    " + clon1.getCodigo() + " " + clon1);
        System.out.println("Clon 2:    " + clon2.getCodigo() + " " + clon2);
        System.out.println("A1 bloqueado heredado en el clon 2 -> " + clon2.getAsiento("A1").getEstado());
        System.out.println("E5 (vendido a Laura en el clon 1) -> clon 1: " + clon1.getAsiento("E5").getEstado()
                + " | clon 2: " + clon2.getAsiento("E5").getEstado()
                + " | plantilla: " + plantilla.getAsiento("E5").getEstado());
        System.out.println("clon1 y clon2 comparten el mapa -> " + clon1.compartenMapa(clon2));
        System.out.println("clon1.E5 == clon2.E5 -> " + (clon1.getAsiento("E5") == clon2.getAsiento("E5")));

        titulo("3. FACTORY METHOD · EmisorEntradas");
        ItemConfiteria comboIndividual = ctx.getConfiteria().buscarItem("K001");
        Promocion martesDeCine = ctx.getConfiteria().buscarPromocion("PR01");
        Compra compraMixta = ctx.getCompras().nuevaCompra(laura)
                .paraFuncion(plantilla)
                .conAsiento("C4")
                .conAsiento("J6")
                .conAsientoRedimido("C5")
                .conProducto(comboIndividual, 1)
                .conPromocion(martesDeCine)
                .build();
        for (Entrada e : compraMixta.getEntradas()) {
            System.out.printf("  %-45s %-20s precio %s  (%s)%n", e.getDescripcion(), e.getClass().getSimpleName(),
                    Formatos.pesos(e.getPrecio()), e.getCodigo());
        }

        titulo("4. BUILDER · Compra.Builder valida antes de crear");
        imprimirCompra(compraMixta);

        try {
            ctx.getCompras().nuevaCompra(laura).conAsiento("A2").build();
            permitido("Compra sin función");
        } catch (CinemaUQException e) {
            rechazado("Compra sin función", e);
        }

        try {
            ctx.getCompras().nuevaCompra(laura).paraFuncion(plantilla).build();
            permitido("Compra vacía");
        } catch (CinemaUQException e) {
            rechazado("Compra vacía", e);
        }

        try {
            ctx.getCompras().nuevaCompra(laura).paraFuncion(plantilla).conAsiento("B3").conAsiento("B3").build();
            permitido("Mismo asiento dos veces");
        } catch (CinemaUQException e) {
            rechazado("Mismo asiento dos veces", e);
        }

        try {
            Compra.Builder builder = ctx.getCompras().nuevaCompra(laura).paraFuncion(plantilla);
            for (int i = 1; i <= 11; i++) {
                builder.conAsiento("D" + i);
            }
            builder.build();
            permitido("11 entradas en una compra");
        } catch (CinemaUQException e) {
            rechazado("11 entradas en una compra", e);
        }

        try {
            Promocion finDeSemana = ctx.getConfiteria().buscarPromocion("PR03");
            ctx.getCompras().nuevaCompra(laura).paraFuncion(plantilla).conAsiento("B4").conPromocion(finDeSemana).build();
            permitido("Promoción de fin de semana un martes (RN-14)");
        } catch (CinemaUQException e) {
            rechazado("Promoción de fin de semana un martes (RN-14)", e);
        }

        try {
            ctx.getCompras().nuevaCompra(andres).paraFuncion(clon1).conAsiento("E5").build();
            permitido("Asiento ya vendido (RN-01)");
        } catch (CinemaUQException e) {
            rechazado("Asiento ya vendido (RN-01)", e);
        }

        titulo("5. PAGO CON TARJETA VIRTUAL Y PUNTOS");
        System.out.println("Laura antes -> saldo " + Formatos.pesos(laura.getTarjeta().getSaldo())
                + ", puntos vigentes " + laura.getCuentaPuntos().getPuntosDisponibles(hoy)
                + " (el lote de 300 puntos de hace 13 meses está vencido)");
        ctx.getCompras().confirmarCompra(compraMixta);
        System.out.println("Laura después -> saldo " + Formatos.pesos(laura.getTarjeta().getSaldo())
                + ", puntos vigentes " + laura.getCuentaPuntos().getPuntosDisponibles(hoy)
                + ", ganó " + compraMixta.getPuntosGanados() + " puntos ("
                + ctx.getCompras().getDescripcionReglaPuntos() + ")");
        System.out.println("Lotes de puntos (se consumió primero el más próximo a vencer):");
        for (AcumulacionPuntos lote : laura.getCuentaPuntos().getAcumulaciones()) {
            String marca = "";
            if (lote.estaVencida(hoy)) {
                marca = "  [VENCIDO]";
            }
            System.out.printf("  %-24s obtenidos %4d  restantes %4d  vence %s%s%n", lote.getReferencia(),
                    lote.getPuntosObtenidos(), lote.getPuntosRestantes(),
                    lote.getFechaVencimiento().format(Formatos.FECHA), marca);
        }

        String casoSaldo = "Andrés paga 3 entradas con saldo " + Formatos.pesos(andres.getTarjeta().getSaldo());
        try {
            Compra compra = ctx.getCompras().nuevaCompra(andres).paraFuncion(plantilla)
                    .conAsiento("F1").conAsiento("F2").conAsiento("F3").build();
            ctx.getCompras().confirmarCompra(compra);
            permitido(casoSaldo);
        } catch (CinemaUQException e) {
            rechazado(casoSaldo, e);
        }

        try {
            Compra compra = ctx.getCompras().nuevaCompra(sofia).paraFuncion(plantilla).conAsiento("F4").build();
            ctx.getCompras().confirmarCompra(compra);
            permitido("Sofía paga con tarjeta bloqueada");
        } catch (CinemaUQException e) {
            rechazado("Sofía paga con tarjeta bloqueada", e);
        }

        try {
            Compra compra = ctx.getCompras().nuevaCompra(andres).paraFuncion(plantilla).conAsientoRedimido("F5").build();
            ctx.getCompras().confirmarCompra(compra);
            permitido("Andrés redime una entrada sin puntos");
        } catch (CinemaUQException e) {
            rechazado("Andrés redime una entrada sin puntos", e);
        }

        titulo("6. RECARGAS (solo el administrador)");
        SolicitudRecarga solicitud = ctx.getTarjetas().solicitudesPendientes().get(0);
        System.out.println("Solicitud pendiente: " + solicitud.getCodigo() + " de " + solicitud.getCliente().getNombre()
                + " por " + Formatos.pesos(solicitud.getValor()));
        ctx.getTarjetas().aprobarSolicitud(admin, solicitud);
        System.out.println("Aprobada -> saldo de Andrés " + Formatos.pesos(andres.getTarjeta().getSaldo())
                + " | movimientos registrados: " + andres.getTarjeta().getMovimientos().size());

        try {
            ctx.getTarjetas().recargar(null, andres, 20_000);
            permitido("Recarga sin administrador");
        } catch (CinemaUQException e) {
            rechazado("Recarga sin administrador", e);
        }

        titulo("7. CANCELACIONES Y REEMBOLSOS (RN-04, RN-05)");
        Compra compraAndres = ctx.getCompras().nuevaCompra(andres).paraFuncion(plantilla).conAsiento("G7").build();
        ctx.getCompras().confirmarCompra(compraAndres);
        Reembolso r100 = ctx.getCancelaciones().cancelarCompra(andres, compraAndres);
        System.out.println("Faltan >24 h -> reembolso " + r100.getPorcentaje() + " % = " + Formatos.pesos(r100.getValor())
                + " | G7 vuelve a estar " + plantilla.getAsiento("G7").getEstado());

        try {
            ctx.getCancelaciones().cancelarCompra(andres, compraAndres);
            permitido("Reembolsar dos veces la misma compra");
        } catch (CinemaUQException e) {
            rechazado("Reembolsar dos veces la misma compra", e);
        }

        Funcion proxima = buscarFuncionDeMenosDe24Horas(ctx, reloj.ahora());
        Compra compraProxima = ctx.getCompras().nuevaCompra(andres).paraFuncion(proxima).conAsiento("B2").build();
        ctx.getCompras().confirmarCompra(compraProxima);
        Reembolso r80 = ctx.getCancelaciones().cancelarCompra(andres, compraProxima);
        long horasFaltantes = Duration.between(reloj.ahora(), proxima.getFechaHora()).toHours();
        System.out.println("Faltan " + horasFaltantes + " h  -> reembolso " + r80.getPorcentaje() + " % = "
                + Formatos.pesos(r80.getValor()) + " de " + Formatos.pesos(compraProxima.getTotal()));

        Compra compraTarde = ctx.getCompras().nuevaCompra(andres).paraFuncion(proxima).conAsiento("B3").build();
        ctx.getCompras().confirmarCompra(compraTarde);
        reloj.avanzar(Duration.ofMinutes(150));
        try {
            ctx.getCancelaciones().cancelarCompra(andres, compraTarde);
            permitido("Cancelar cuando faltan 1 h 30 min");
        } catch (CinemaUQException e) {
            rechazado("Cancelar cuando faltan 1 h 30 min", e);
        }

        titulo("8. EL CINE CANCELA UNA FUNCIÓN -> 100 % a todos");
        long saldoAntes = laura.getTarjeta().getSaldo();
        List<Reembolso> reembolsos = ctx.getCancelaciones().cancelarFuncion(admin, clon1);
        System.out.println("Reembolsos generados: " + reembolsos.size() + " | Laura recupera "
                + Formatos.pesos(laura.getTarjeta().getSaldo() - saldoAntes)
                + " | asientos vendidos en la función: " + clon1.contarAsientos(EstadoAsiento.VENDIDO));

        try {
            ctx.getCompras().nuevaCompra(laura).paraFuncion(clon1).conAsiento("A5").build();
            permitido("Comprar en la función cancelada");
        } catch (CinemaUQException e) {
            rechazado("Comprar en la función cancelada", e);
        }

        System.out.println();
        System.out.println("Fin de la demostración.");
    }

    private static Funcion buscarFuncionDeMenosDe24Horas(ContextoAplicacion ctx, LocalDateTime ahora) {
        for (Funcion funcion : ctx.getCartelera().listarFunciones()) {
            long horas = Duration.between(ahora, funcion.getFechaHora()).toHours();
            if (horas < 24) {
                return funcion;
            }
        }
        throw new IllegalStateException("Los datos iniciales deben tener una función en menos de 24 horas");
    }

    private static void imprimirCompra(Compra c) {
        System.out.println("Compra " + c.getCodigo() + " (" + c.getEstado() + ")");
        System.out.println("  Subtotal entradas   " + Formatos.pesos(c.getSubtotalEntradas()));
        System.out.println("  Subtotal confitería " + Formatos.pesos(c.getSubtotalConfiteria()));
        System.out.println("  Descuento           " + Formatos.pesos(c.getDescuento()));
        System.out.println("  Total a pagar       " + Formatos.pesos(c.getTotal()));
        System.out.println("  Puntos a redimir    " + c.getPuntosRedimidos());
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }

    private static void permitido(String caso) {
        System.out.println("  [" + caso + "] -> se permitió");
    }

    private static void rechazado(String caso, CinemaUQException e) {
        System.out.println("  [" + caso + "] -> RECHAZADO: " + e.getClass().getSimpleName() + ": " + e.getMessage());
    }
}