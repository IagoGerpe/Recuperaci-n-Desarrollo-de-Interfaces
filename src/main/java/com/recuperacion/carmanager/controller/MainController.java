package com.recuperacion.carmanager.controller;

import com.recuperacion.carmanager.AppShell;
import com.recuperacion.carmanager.model.User;
import com.recuperacion.carmanager.utils.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    private Label contentLabel;

    @FXML
    private Button usersButton;

    @FXML
    private void initialize() {
        User currentUser = Session.getCurrentUser();

        if (currentUser != null) {
            contentLabel.setText("Bienvenido, " + currentUser.getUsername());
        }

        if (Session.isAdmin()) {
            usersButton.setVisible(true);
            usersButton.setManaged(true);
        }
    }

    @FXML
    private void handleShowCars() {
        contentLabel.setText("CarView");
    }

    @FXML
    private void handleShowUsers() {
        contentLabel.setText("UserView");
    }

    @FXML
    private void handleLogout() {
        AppShell.showLoginView();
    }
}