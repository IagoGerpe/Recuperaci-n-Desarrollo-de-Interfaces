package com.recuperacion.carmanager.controller;

import com.recuperacion.carmanager.model.Leaderboard;
import com.recuperacion.carmanager.dao.FavoritoDAO;
import com.recuperacion.carmanager.model.Car;
import com.recuperacion.carmanager.dao.CarDAO;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.List;

public class LeaderboardController {

    private final FavoritoDAO favoritoDAO = new FavoritoDAO();
    private final CarDAO carDAO = new CarDAO();

    @FXML
    private BorderPane leaderboard;

    @FXML
    private VBox leaderboardRowsBox;

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() { //método inicial que carga la vista
        loadLeaderboard();
    }

    private void loadLeaderboard() { //este método carga la clasificación desde la base de datos para crear las filas
        List<Leaderboard> entries = favoritoDAO.findLeaderboardEntries();

        leaderboardRowsBox.getChildren().clear(); //con esto evitamos que al cargarlo dos veces se duplique todo

        if (entries.isEmpty()) {
            messageLabel.setText("No hay coches registrados todavía.");
            return;
        }

        for (Leaderboard entry : entries) {
            HBox row = createLeaderboardRow(entry);
            leaderboardRowsBox.getChildren().add(row);
        }

        messageLabel.setText("Mostrando " + entries.size() + " coches en la clasificación.");
    }

    private HBox createLeaderboardRow(Leaderboard entry) { //método que crea cada fila de vehículo individualmente
        HBox row = new HBox(18);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("leaderboard-row");
        row.setCursor(Cursor.HAND);
        row.setOnMouseClicked(event -> openCarDetails(entry));

        if (entry.getPosition() == 1) {
            row.getStyleClass().add("leaderboard-first-row");
        }

        Label positionLabel = new Label("#" + entry.getPosition());
        positionLabel.getStyleClass().add("leaderboard-position-label");

        Node carImage = createCarImage(entry.getImagePath());

        Label nameLabel = new Label(entry.getCarName());
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        nameLabel.getStyleClass().add("leaderboard-name-label");
        HBox.setHgrow(nameLabel, javafx.scene.layout.Priority.ALWAYS);

        Label votesLabel = new Label(entry.getVotes() + " votos");
        votesLabel.getStyleClass().add("leaderboard-votes-label");

        row.getChildren().addAll(positionLabel, carImage, nameLabel, votesLabel);

        return row;
    }

    private Node createCarImage(String imagePath) {
        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("leaderboard-image-container");

        URL imageUrl = getImageUrl(imagePath);

        if (imageUrl == null) {
            Label placeholderLabel = new Label("Foto");
            placeholderLabel.getStyleClass().add("leaderboard-image-placeholder");
            imageContainer.getChildren().add(placeholderLabel);
            return imageContainer;
        }

        Image image = new Image(imageUrl.toExternalForm());

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(82);
        imageView.setFitHeight(52);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        imageContainer.getChildren().add(imageView);

        return imageContainer;
    }

    private URL getImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        String normalizedPath = imagePath.startsWith("/")
                ? imagePath
                : "/" + imagePath;

        return LeaderboardController.class.getResource(normalizedPath);
    }

    private void openCarDetails(Leaderboard entry) { //mismo metodo que en el carview, abre la pestaña de detalles
        try {
            Car car = carDAO.findById(entry.getCarId());

            if (car == null) {
                messageLabel.setText("No se pudo encontrar el coche seleccionado.");
                return;
            }

            URL fxmlUrl = LeaderboardController.class.getResource("/fxml/car-details-view.fxml");

            if (fxmlUrl == null) {
                throw new IllegalStateException("No se encontró la vista de detalle del coche.");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent detailView = loader.load();

            CarDetailsController controller = loader.getController();
            controller.setCar(car);

            if (leaderboard.getParent() instanceof StackPane) {
                StackPane contentPane = (StackPane) leaderboard.getParent();
                contentPane.getChildren().setAll(detailView);
            } else {
                throw new IllegalStateException("No se pudo localizar el contenedor principal.");
            }

        } catch (Exception exception) {
            messageLabel.setText("No se pudo abrir el detalle del coche.");
            System.out.println("Error al abrir detalle desde clasificación: " + exception.getMessage());
        }
    }
}
