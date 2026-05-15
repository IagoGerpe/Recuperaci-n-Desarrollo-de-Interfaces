package com.recuperacion.carmanager;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class AppShell {

    private static Stage primaryStage;

    private AppShell() {
    }

    public static void initialize(Stage stage) {
        primaryStage = stage;
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(500);
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

            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
            addStylesheet(scene);

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();

            maximizeStage();

            Platform.runLater(AppShell::maximizeStage);

        } catch (IOException exception) {
            throw new RuntimeException("No se pudo cargar la vista: " + fxmlPath, exception);
        }
    }

    private static void maximizeStage() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

        primaryStage.setIconified(false);
        primaryStage.setFullScreen(false);
        primaryStage.setMaximized(false);

        primaryStage.setX(visualBounds.getMinX());
        primaryStage.setY(visualBounds.getMinY());
        primaryStage.setWidth(visualBounds.getWidth());
        primaryStage.setHeight(visualBounds.getHeight());

        primaryStage.setMaximized(true);
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