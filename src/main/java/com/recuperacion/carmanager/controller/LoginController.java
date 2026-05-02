package com.recuperacion.carmanager.controller;

import com.recuperacion.carmanager.AppShell;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            messageLabel.setText("Introduce usuario y contraseña.");
            return;
        }

        //TODO mysql
        AppShell.showMainView();
    }

    @FXML
    private void handleGoToRegister() {
        AppShell.showRegisterView();
    }
}