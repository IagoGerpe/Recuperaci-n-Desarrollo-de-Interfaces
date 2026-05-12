package com.recuperacion.carmanager.controller;

import com.recuperacion.carmanager.AppShell;
import com.recuperacion.carmanager.model.User;
import com.recuperacion.carmanager.utils.Session;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML
    private Button usersButton;

    @FXML
    private Label welcomeLabel;

    @FXML
    private StackPane contentPane;

    @FXML
    private void initialize() {
        User currentUser = Session.getCurrentUser();

        if (currentUser != null) {
            welcomeLabel.setText("Bienvenido, " + currentUser.getUsername());
        }

        boolean isAdmin = Session.isAdmin();

        usersButton.setVisible(isAdmin);
        usersButton.setManaged(isAdmin);

        loadView("/fxml/cars-view.fxml");
    }

    @FXML
    private void handleShowCars() {
        loadView("/fxml/cars-view.fxml");
    }

    @FXML
    private void handleShowUsers() {
        if (!Session.isAdmin()) {
            showMessage("No tienes permisos para acceder a la gestión de usuarios.");
            return;
        }

        loadView("/fxml/user-view.fxml");
    }

    @FXML
    private void handleLogout() {
        Session.clear();
        AppShell.showLoginView();
    }

    private void loadView(String fxmlPath) {
        try {
            URL fxmlUrl = MainController.class.getResource(fxmlPath);

            if (fxmlUrl == null) {
                throw new IllegalStateException("No se encontró la vista: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent view = loader.load();

            contentPane.getChildren().setAll(view);

        } catch (IOException exception) {
            showMessage("No se pudo cargar la vista.");
            System.out.println("Error al cargar la vista " + fxmlPath + ": " + exception.getMessage());
        }
    }

    private void showMessage(String message) {
        Label label = new Label(message);
        label.getStyleClass().add("subtitle-label");

        contentPane.getChildren().setAll(label);
    }
}