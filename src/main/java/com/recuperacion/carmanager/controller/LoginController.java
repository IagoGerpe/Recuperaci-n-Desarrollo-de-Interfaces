package com.recuperacion.carmanager.controller;

import com.recuperacion.carmanager.AppShell;
import com.recuperacion.carmanager.dao.UserDAO;
import com.recuperacion.carmanager.model.User;
import com.recuperacion.carmanager.utils.PasswordUtil;
import com.recuperacion.carmanager.utils.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private final UserDAO userDAO = new UserDAO();

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

        User user = userDAO.findByUsername(username);

        if (user == null) {
            messageLabel.setText("Usuario o contraseña incorrectos.");
            return;
        }

        boolean validPassword = PasswordUtil.checkPassword(password, user.getPassword());

        if (!validPassword) {
            messageLabel.setText("Usuario o contraseñña incorrectos.");
            return;
        }

        Session.setCurrentUser(user);
        AppShell.showMainView();
    }

    @FXML
    private void handleGoToRegister() {
        AppShell.showRegisterView();
    }
}