package co.edu.uniquindio.cinemauq.modelo.usuarios;


import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.puntos.CuentaPuntos;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.TarjetaVirtual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente extends Usuario {

    private final TarjetaVirtual tarjeta;
    private final CuentaPuntos cuentaPuntos;
    private final List<Compra> compras = new ArrayList<>();

    public Cliente(String cedula, String nombre, String correo, String contrasena) {
        super(cedula, nombre, correo, contrasena);
        this.tarjeta = new TarjetaVirtual("TV-" + cedula);
        this.cuentaPuntos = new CuentaPuntos();
    }

    public void agregarCompra(Compra compra) {
        compras.add(compra);
    }

    public List<Compra> getCompras() {
        return Collections.unmodifiableList(compras);
    }

    public TarjetaVirtual getTarjeta() {
        return tarjeta;
    }

    public CuentaPuntos getCuentaPuntos() {
        return cuentaPuntos;
    }

    @Override
    public String getRol() {
        return "CLIENTE";
    }
}