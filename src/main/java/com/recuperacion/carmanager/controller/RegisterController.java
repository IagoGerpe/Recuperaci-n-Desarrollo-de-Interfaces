package com.recuperacion.carmanager.controller;

import com.recuperacion.carmanager.AppShell;
import com.recuperacion.carmanager.dao.UserDAO;
import com.recuperacion.carmanager.model.User;
import com.recuperacion.carmanager.utils.PasswordUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField checkPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String checkPassword = checkPasswordField.getText();

        if (!isFormValid(username, email, password, checkPassword)) {
            return;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User(
                username,
                email,
                hashedPassword,
                "user"
        );

        boolean saved = userDAO.save(user);

        if (saved) {
            messageLabel.setText("Usuario registrado correctamente.");
            clearFields();
        } else {
            messageLabel.setText("No se puede registrar al usuario.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        AppShell.showLoginView();
    }

    private boolean isFormValid(String username, String email, String password, String checkPassword) {
        if (username.isBlank() || email.isBlank() || password.isBlank() || checkPassword.isBlank()) {
            messageLabel.setText("Todos los campos son obligotorios.");
            return false;
        }

        if (!email.contains("@") || !email.contains(".")) {
            messageLabel.setText("Introduce un email válido.");
            return false;
        }

        if (userDAO.existsByUsername(username)) {
            messageLabel.setText("El nombre de usuario ya existe.");
            return false;
        }

        if (userDAO.existsByEmail(email)) {
            messageLabel.setText("El email ya está en la base de datos.");
            return false;
        }

        if (password.length() < 6) {
            messageLabel.setText("La contraseña debe tener minimo 6 caracteres.");
            return false;
        }

        if (!containsLetter(password)) {
            messageLabel.setText("La contraseña debe tener   al menos una lerta.");
            return false;
        }

        if (!containsNumber(password)) {
            messageLabel.setText("La contraseña debe contener al menos un número.");
            return false;
        }

        if (!password.equals(checkPassword)) {
            messageLabel.setText("Las contraseñas no son iguaales.");
            return false;
        }

        return true;
    }

    private boolean containsLetter(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isLetter(text.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    private boolean containsNumber(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isDigit(text.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    private void clearFields() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        checkPasswordField.clear();
    }
}