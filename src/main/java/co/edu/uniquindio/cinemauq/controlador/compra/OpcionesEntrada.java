package co.edu.uniquindio.cinemauq.controlador.compra;

import co.edu.uniquindio.cinemauq.modelo.enums.AdicionalEntrada;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoSala;
import co.edu.uniquindio.cinemauq.util.Formatos;
import javafx.scene.control.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class OpcionesEntrada extends ComponenteCompra {

    private final CheckBox chkConPuntos;
    private final CheckBox chkGafas3D;
    private final CheckBox chkSillaVibratoria;

    public OpcionesEntrada(CheckBox chkConPuntos, CheckBox chkGafas3D, CheckBox chkSillaVibratoria) {
        this.chkConPuntos = chkConPuntos;
        this.chkGafas3D = chkGafas3D;
        this.chkSillaVibratoria = chkSillaVibratoria;
        chkGafas3D.setText(etiqueta(AdicionalEntrada.GAFAS_3D));
        chkSillaVibratoria.setText(etiqueta(AdicionalEntrada.SILLA_VIBRATORIA));
        chkConPuntos.setOnAction(evento -> avisar(MediadorCompra.OPCIONES_CAMBIARON));
        chkGafas3D.setOnAction(evento -> avisar(MediadorCompra.OPCIONES_CAMBIARON));
        chkSillaVibratoria.setOnAction(evento -> avisar(MediadorCompra.OPCIONES_CAMBIARON));
    }

    private static String etiqueta(AdicionalEntrada adicional) {
        return adicional.getNombre() + " (+" + Formatos.pesos(adicional.getValor()) + " c/u)";
    }

    public void ajustarASala(TipoSala tipoSala) {
        ajustar(chkGafas3D, AdicionalEntrada.GAFAS_3D, tipoSala);
        ajustar(chkSillaVibratoria, AdicionalEntrada.SILLA_VIBRATORIA, tipoSala);
    }

    private void ajustar(CheckBox casilla, AdicionalEntrada adicional, TipoSala tipoSala) {
        boolean permitido = tipoSala != null && adicional.permitidoEn(tipoSala);
        casilla.setDisable(!permitido);
        if (!permitido) {
            casilla.setSelected(false);
        }
    }

    public boolean isConPuntos() {
        return chkConPuntos.isSelected();
    }

    public List<AdicionalEntrada> getAdicionales() {
        List<AdicionalEntrada> adicionales = new ArrayList<>();
        if (chkGafas3D.isSelected()) {
            adicionales.add(AdicionalEntrada.GAFAS_3D);
        }
        if (chkSillaVibratoria.isSelected()) {
            adicionales.add(AdicionalEntrada.SILLA_VIBRATORIA);
        }
        return adicionales;
    }

    public void limpiar() {
        chkConPuntos.setSelected(false);
        chkGafas3D.setSelected(false);
        chkSillaVibratoria.setSelected(false);
    }
}
