package co.edu.uniquindio.cinemauq.datos;

import co.edu.uniquindio.cinemauq.app.ContextoAplicacion;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.cartelera.Sala;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.Promocion;
import co.edu.uniquindio.cinemauq.modelo.confiteria.Combo;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ItemCombo;
import co.edu.uniquindio.cinemauq.modelo.confiteria.Producto;
import co.edu.uniquindio.cinemauq.modelo.enums.AlcancePromocion;
import co.edu.uniquindio.cinemauq.modelo.enums.CategoriaProducto;
import co.edu.uniquindio.cinemauq.modelo.enums.ClasificacionEdad;
import co.edu.uniquindio.cinemauq.modelo.enums.GeneroPelicula;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoSala;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Administrador;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.servicio.ServicioCartelera;
import co.edu.uniquindio.cinemauq.servicio.ServicioConfiteria;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public final class DatosIniciales {
    private DatosIniciales() {
    }

    public static void cargar(ContextoAplicacion ctx) {
        LocalDateTime ahora = ctx.getReloj().ahora();
        LocalDate hoy = ahora.toLocalDate();

        Administrador admin = ctx.getAutenticacion()
                .registrarAdministrador("1094000001", "Administrador CinemaUQ", "admin@cinemauq.co", "admin123");
        Cliente laura = ctx.getAutenticacion()
                .registrarCliente("1094111222", "Laura Gómez", "laura@correo.com", "laura123");
        Cliente andres = ctx.getAutenticacion()
                .registrarCliente("1094333444", "Andrés Ramírez", "andres@correo.com", "andres123");
        Cliente sofia = ctx.getAutenticacion()
                .registrarCliente("1094555666", "Sofía Patiño", "sofia@correo.com", "sofia123");

        ctx.getTarjetas().recargar(admin, laura, 200_000);
        ctx.getTarjetas().recargar(admin, andres, 15_000);
        ctx.getTarjetas().recargar(admin, sofia, 50_000);
        ctx.getTarjetas().bloquearTarjeta(admin, sofia);
        ctx.getTarjetas().solicitarRecarga(andres, 40_000);

        laura.getCuentaPuntos().acumular(100, hoy.minusMonths(11), "Saldo inicial 1");
        laura.getCuentaPuntos().acumular(160, hoy.minusMonths(2), "Saldo inicial 2");
        laura.getCuentaPuntos().acumular(300, hoy.minusMonths(13), "Saldo inicial vencido");

        ServicioCartelera cartelera = ctx.getCartelera();
        Pelicula dune = new Pelicula("P001", "Dune: Parte Dos", GeneroPelicula.CIENCIA_FICCION,
                ClasificacionEdad.MAYORES_12, 166, "Paul Atreides se une a los Fremen.");
        Pelicula intensamente = new Pelicula("P002", "Intensamente 2", GeneroPelicula.ANIMACION,
                ClasificacionEdad.TODO_PUBLICO, 96, "Riley entra a la adolescencia y llegan nuevas emociones.");
        Pelicula alien = new Pelicula("P003", "Alien: Romulus", GeneroPelicula.TERROR,
                ClasificacionEdad.MAYORES_15, 119, "Jóvenes colonos se enfrentan a la forma de vida más aterradora.");
        Pelicula encanto = new Pelicula("P004", "Encanto", GeneroPelicula.ANIMACION,
                ClasificacionEdad.TODO_PUBLICO, 102, "La familia Madrigal y su casa mágica en Colombia.");
        cartelera.registrarPelicula(dune);
        cartelera.registrarPelicula(intensamente);
        cartelera.registrarPelicula(alien);
        cartelera.registrarPelicula(encanto);

        Sala sala1 = new Sala(1, "Sala 1", TipoSala.DOS_D, 8, 10, 2);
        Sala sala2 = new Sala(2, "Sala 2", TipoSala.TRES_D, 6, 8, 1);
        Sala sala3 = new Sala(3, "Sala IMAX", TipoSala.IMAX, 10, 12, 3);
        cartelera.registrarSala(sala1);
        cartelera.registrarSala(sala2);
        cartelera.registrarSala(sala3);

        LocalDate manana = hoy.plusDays(1);

        Funcion duneBase = cartelera.programarFuncion(dune, sala3, manana.atTime(LocalTime.of(15, 0)), 22_000);
        cartelera.bloquearAsiento(duneBase, "A1");
        cartelera.bloquearAsiento(duneBase, "A12");
        List<LocalDateTime> fechasDune = new ArrayList<>();
        fechasDune.add(manana.atTime(LocalTime.of(19, 0)));
        fechasDune.add(manana.plusDays(1).atTime(LocalTime.of(19, 0)));
        List<Funcion> copiasDune = cartelera.replicarFuncion(duneBase, fechasDune);

        Funcion intensaBase = cartelera.programarFuncion(intensamente, sala1, manana.atTime(LocalTime.of(14, 0)), 14_000);
        List<LocalDateTime> fechasIntensamente = new ArrayList<>();
        fechasIntensamente.add(manana.atTime(LocalTime.of(16, 30)));
        fechasIntensamente.add(manana.plusDays(1).atTime(LocalTime.of(14, 0)));
        cartelera.replicarFuncion(intensaBase, fechasIntensamente);

        cartelera.programarFuncion(alien, sala2, manana.atTime(LocalTime.of(21, 0)), 18_000);
        cartelera.programarFuncion(encanto, sala1, manana.plusDays(2).atTime(LocalTime.of(11, 0)), 12_000);

        LocalDateTime enCuatroHoras = ahora.plusHours(4).withMinute(0).withSecond(0).withNano(0);
        cartelera.programarFuncion(encanto, sala2, enCuatroHoras, 12_000);

        ServicioConfiteria confiteria = ctx.getConfiteria();
        Producto crispetasM = new Producto("C001", "Crispetas medianas", CategoriaProducto.CRISPETAS, 12_000, 110);
        Producto crispetasG = new Producto("C002", "Crispetas grandes", CategoriaProducto.CRISPETAS, 15_000, 140);
        Producto gaseosa = new Producto("C003", "Gaseosa 16 oz", CategoriaProducto.BEBIDA, 7_000, 65);
        Producto agua = new Producto("C004", "Agua 600 ml", CategoriaProducto.BEBIDA, 4_500, 40);
        Producto nachos = new Producto("C005", "Nachos con queso", CategoriaProducto.SNACK, 13_000, 120);
        Producto perro = new Producto("C006", "Perro caliente", CategoriaProducto.COMIDA, 11_000, 100);
        Producto chocolatina = new Producto("C007", "Chocolatina", CategoriaProducto.DULCE, 4_000, 35);
        confiteria.registrarItem(crispetasM);
        confiteria.registrarItem(crispetasG);
        confiteria.registrarItem(gaseosa);
        confiteria.registrarItem(agua);
        confiteria.registrarItem(nachos);
        confiteria.registrarItem(perro);
        confiteria.registrarItem(chocolatina);

        List<ItemCombo> individual = new ArrayList<>();
        individual.add(new ItemCombo(crispetasM, 1));
        individual.add(new ItemCombo(gaseosa, 1));
        confiteria.registrarItem(new Combo("K001", "Combo Individual", 17_000, 150, individual));

        List<ItemCombo> pareja = new ArrayList<>();
        pareja.add(new ItemCombo(crispetasG, 1));
        pareja.add(new ItemCombo(gaseosa, 2));
        pareja.add(new ItemCombo(chocolatina, 1));
        Combo comboPareja = new Combo("K002", "Combo Pareja", 32_000, 280, pareja);
        confiteria.registrarItem(comboPareja);

        List<ItemCombo> comboNachos = new ArrayList<>();
        comboNachos.add(new ItemCombo(nachos, 1));
        comboNachos.add(new ItemCombo(gaseosa, 1));
        confiteria.registrarItem(new Combo("K003", "Combo Nachos", 18_000, 160, comboNachos));

        confiteria.registrarPromocion(new Promocion("PR01", "Martes de cine", 30, AlcancePromocion.ENTRADAS,
                hoy.minusDays(30), hoy.plusDays(180), EnumSet.of(DayOfWeek.TUESDAY)));
        confiteria.registrarPromocion(new Promocion("PR02", "Confitería UQ", 15, AlcancePromocion.CONFITERIA,
                hoy.minusDays(5), hoy.plusDays(60), EnumSet.noneOf(DayOfWeek.class)));
        confiteria.registrarPromocion(new Promocion("PR03", "Fin de semana en familia", 10, AlcancePromocion.TODO,
                hoy.minusDays(5), hoy.plusDays(60), EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)));

        Funcion duneNoche = copiasDune.get(0);
        Compra compraLaura = ctx.getCompras().nuevaCompra(laura)
                .paraFuncion(duneNoche)
                .conAsiento("E5")
                .conAsiento("E6")
                .conProducto(comboPareja, 1)
                .build();
        ctx.getCompras().confirmarCompra(compraLaura);
    }
}