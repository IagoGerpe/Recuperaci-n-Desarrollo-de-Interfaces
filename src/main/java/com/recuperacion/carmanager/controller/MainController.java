package com.recuperacion.carmanager.controller;

import com.recuperacion.carmanager.AppShell;
import com.recuperacion.carmanager.model.User;
import com.recuperacion.carmanager.utils.Session;
import com.recuperacion.carmanager.utils.RememberSessionUtil;

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
    } //para enseñar la vista de los coches

    @FXML
    private void handleShowUsers() { //para ir a la pestaña de ususarios (solo admins)
        if (!Session.isAdmin()) {
            showMessage("No tienes permisos para acceder a la gestión de usuarios.");
            return;
        }

        loadView("/fxml/user-view.fxml");
    }

    @FXML
    private void handleShowLeaderboard() { //para ir a la pestaña de clasificacion
        loadView("/fxml/leaderboard-view.fxml");
    }

    @FXML
    private void handleLogout() { //cerrar sesion
        RememberSessionUtil.clearRememberedUser();
        Session.clear();
        AppShell.showLoginView();
    }

    private void loadView(String fxmlPath) { //método central de navegación, cambia entre escenas recibiendo la ruta del fxml
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

    private void showMessage(String message) { //método de crear un label utilizado para enseñar un mensaje en el contentpane
        Label label = new Label(message);
        label.getStyleClass().add("subtitle-label");

        contentPane.getChildren().setAll(label);
    }
}