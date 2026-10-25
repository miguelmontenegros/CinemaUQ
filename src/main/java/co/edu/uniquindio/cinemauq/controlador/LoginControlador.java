package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginControlador extends ControladorBase {

    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private TextField txtRegCedula;
    @FXML
    private TextField txtRegNombre;
    @FXML
    private TextField txtRegCorreo;
    @FXML
    private PasswordField txtRegContrasena;

    @Override
    protected void alCargar() {
    }

    @FXML
    private void iniciarSesion() {
        try {
            fachada.iniciarSesion(txtCorreo.getText(), txtContrasena.getText());
            if (fachada.esAdministrador()) {
                navegador.irAAdministrador();
            } else {
                navegador.irACliente();
            }
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void registrarse() {
        try {
            Cliente cliente = fachada.registrarCliente(txtRegCedula.getText(), txtRegNombre.getText(),
                    txtRegCorreo.getText(), txtRegContrasena.getText());
            mostrarInfo("Bienvenido(a) " + cliente.getNombre() + ". Su tarjeta virtual " + cliente.getTarjeta().getNumero()
                    + " fue creada con saldo $0. Ya puede iniciar sesión.");
            txtCorreo.setText(cliente.getCorreo());
            txtRegCedula.clear();
            txtRegNombre.clear();
            txtRegCorreo.clear();
            txtRegContrasena.clear();
        } catch (CinemaUQException e) {
            mostrarError(e.getMessage());
        }
    }
}