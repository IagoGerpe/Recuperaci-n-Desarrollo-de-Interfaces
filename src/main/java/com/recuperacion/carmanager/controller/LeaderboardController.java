package com.recuperacion.carmanager.controller;

import com.recuperacion.carmanager.dao.FavoritoDAO;
import com.recuperacion.carmanager.model.Leaderboard;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;

public class LeaderboardController {

    private final FavoritoDAO favoritoDAO = new FavoritoDAO();

    @FXML
    private VBox leaderboardRowsBox;

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        loadLeaderboard();
    }

    private void loadLeaderboard() {
        List<Leaderboard> entries = favoritoDAO.findLeaderboardEntries();

        leaderboardRowsBox.getChildren().clear();

        if (entries.isEmpty()) {
            messageLabel.setText("No hay coches registrados todavía.");
            return;
        }

        for (Leaderboard entry : entries) {
            HBox row = createLeaderboardRow(entry);
            leaderboardRowsBox.getChildren().add(row);
        }

        messageLabel.setText("Mostrando " + entries.size() + " coches en la clasificaciónn.");
    }

    private HBox createLeaderboardRow(Leaderboard entry) {
        HBox row = new HBox(18);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("leaderboard-row");

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
}
