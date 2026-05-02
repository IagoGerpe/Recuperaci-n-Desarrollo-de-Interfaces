package com.recuperacion.carmanager.controller;

import com.recuperacion.carmanager.AppShell;
import javafx.fxml.FXML;

public class RegisterController {

    @FXML
    private void handleBackToLogin() {
        AppShell.showLoginView();
    }
}