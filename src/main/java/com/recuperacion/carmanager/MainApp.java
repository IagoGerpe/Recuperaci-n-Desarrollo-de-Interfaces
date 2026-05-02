package com.recuperacion.carmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApp.class.getResource("/fxml/hello-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 900, 550);
        scene.getStylesheets().add(
                MainApp.class.getResource("/css/styles.css").toExternalForm()
        );

        stage.setTitle("Recuperación Desarrollo de Interfaces");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
