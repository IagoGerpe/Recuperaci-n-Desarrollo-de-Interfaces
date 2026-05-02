package com.recuperacion.carmanager.controller;

import com.recuperacion.carmanager.AppShell;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    private Label contentLabel;

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