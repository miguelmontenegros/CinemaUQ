module co.edu.uniquindio.cinemauq {
    requires javafx.controls;
    requires javafx.fxml;

    opens co.edu.uniquindio.cinemauq.application to javafx.graphics, javafx.fxml;
    opens co.edu.uniquindio.cinemauq.controllers to javafx.fxml;

    exports co.edu.uniquindio.cinemauq.application;
    exports co.edu.uniquindio.cinemauq.controllers;
}