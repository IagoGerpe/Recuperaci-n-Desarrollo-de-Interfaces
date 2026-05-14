package com.recuperacion.carmanager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class AppShell {

    private static final double DEFAULT_WIDTH = 900;
    private static final double DEFAULT_HEIGHT = 550;

    private static Stage primaryStage;

    private AppShell() {
    }

    public static void initialize(Stage stage) {
        primaryStage = stage;
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.setMaximized(true);
    }

    public static void showLoginView() {
        setScene("/fxml/login-view.fxml", "Login - Car Manager App");
    }

    public static void showRegisterView() {
        setScene("/fxml/register-view.fxml", "Registro - Car Manager App");
    }

    public static void showMainView() {
        setScene("/fxml/main-view.fxml", "Car Manager App");
    }

    private static void setScene(String fxmlPath, String title) {
        checkStageIsInitialized();

        try {
            URL fxmlUrl = AppShell.class.getResource(fxmlPath);

            if (fxmlUrl == null) {
                throw new IllegalStateException("No se ha encontrado el archivo FXML: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            addStylesheet(scene);

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();

        } catch (IOException exception) {
            throw new RuntimeException("No se pudo cargar la vista: " + fxmlPath, exception);
        }
    }

    private static void addStylesheet(Scene scene) {
        URL cssUrl = AppShell.class.getResource("/css/styles.css");

        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    private static void checkStageIsInitialized() {
        if (primaryStage == null) {
            throw new IllegalStateException("AppShell no ha sido inicializado con un Stage.");
        }
    }
}