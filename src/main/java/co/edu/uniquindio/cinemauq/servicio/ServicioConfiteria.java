package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.EntidadNoEncontradaException;
import co.edu.uniquindio.cinemauq.excepciones.RegistroDuplicadoException;
import co.edu.uniquindio.cinemauq.modelo.compra.Promocion;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;

import java.util.ArrayList;
import java.util.List;

public class ServicioConfiteria {

    private final Repositorio<ItemConfiteria> items;
    private final Repositorio<Promocion> promociones;
    private final Reloj reloj;

    public ServicioConfiteria(Repositorio<ItemConfiteria> items, Repositorio<Promocion> promociones, Reloj reloj) {
        this.items = items;
        this.promociones = promociones;
        this.reloj = reloj;
    }

    public void registrarItem(ItemConfiteria item) {
        if (items.existe(item.getCodigo())) {
            throw new RegistroDuplicadoException("Ya existe un producto con código " + item.getCodigo());
        }
        items.guardar(item);
    }

    public List<ItemConfiteria> itemsDisponibles() {
        List<ItemConfiteria> disponibles = new ArrayList<>();
        for (ItemConfiteria item : items.listar()) {
            if (item.isActivo()) {
                disponibles.add(item);
            }
        }
        return disponibles;
    }

    public ItemConfiteria buscarItem(String codigo) {
        ItemConfiteria item = items.buscar(codigo);
        if (item == null) {
            throw new EntidadNoEncontradaException("No existe el producto o combo " + codigo);
        }
        return item;
    }

    public List<ItemConfiteria> listarItems() {
        return items.listar();
    }

    public void registrarPromocion(Promocion promocion) {
        if (promociones.existe(promocion.getCodigo())) {
            throw new RegistroDuplicadoException("Ya existe una promoción con código " + promocion.getCodigo());
        }
        promociones.guardar(promocion);
    }

    public List<Promocion> promocionesVigentes() {
        List<Promocion> vigentes = new ArrayList<>();
        for (Promocion promocion : promociones.listar()) {
            if (promocion.estaVigente(reloj.ahora())) {
                vigentes.add(promocion);
            }
        }
        return vigentes;
    }

    public Promocion buscarPromocion(String codigo) {
        Promocion promocion = promociones.buscar(codigo);
        if (promocion == null) {
            throw new EntidadNoEncontradaException("No existe la promoción " + codigo);
        }
        return promocion;
    }

    public List<Promocion> listarPromociones() {
        return promociones.listar();
    }
}
