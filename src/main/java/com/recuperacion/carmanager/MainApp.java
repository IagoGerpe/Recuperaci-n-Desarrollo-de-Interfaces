package com.recuperacion.carmanager;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        AppShell.initialize(stage);
        AppShell.showLoginView();
    }

    public static void main(String[] args) {
        launch(args);
    }
}