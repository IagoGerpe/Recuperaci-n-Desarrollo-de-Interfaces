package com.recuperacion.carmanager.controller;

import com.recuperacion.carmanager.dao.CarDAO;
import com.recuperacion.carmanager.model.Car;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;

public class CarsController {

    private final CarDAO carDAO = new CarDAO();

    @FXML
    private ComboBox<String> typeFilterComboBox;

    @FXML
    private TilePane carsTilePane;

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        loadCarTypes();
        loadCars();
    }

    @FXML
    private void handleFilterCars() {
        String selectedType = typeFilterComboBox.getValue();

        if (selectedType == null || selectedType.isBlank()) {
            messageLabel.setText("Selecciona un tipo de coche para filtrar.");
            return;
        }

        List<Car> filteredCars = carDAO.findByType(selectedType);
        showCars(filteredCars);

        messageLabel.setText("Filtro aplicado: " + selectedType);
    }

    @FXML
    private void handleClearFilter() {
        typeFilterComboBox.setValue(null);
        loadCars();
    }

    private void loadCarTypes() {
        List<String> carTypes = carDAO.findAllCarTypes();
        typeFilterComboBox.getItems().setAll(carTypes);
    }

    private void loadCars() {
        List<Car> cars = carDAO.findAll();
        showCars(cars);

        if (cars.isEmpty()) {
            messageLabel.setText("No hay coches registrados todavía.");
        } else {
            messageLabel.setText("Mostrando " + cars.size() + " coches.");
        }
    }

    private void showCars(List<Car> cars) {
        carsTilePane.getChildren().clear();

        for (Car car : cars) {
            VBox card = createCarCard(car);
            carsTilePane.getChildren().add(card);
        }
    }

    private VBox createCarCard(Car car) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.setPadding(new Insets(16));
        card.setPrefWidth(230);
        card.setMinHeight(260);
        card.getStyleClass().add("car-card");

        Node carImage = createCarImage(car.getImagePath());

        Label titleLabel = new Label(car.getFullName());
        titleLabel.getStyleClass().add("car-card-title");

        Label typeLabel = new Label("Tipo: " + car.getCarType());
        typeLabel.getStyleClass().add("car-card-text");

        Label powerLabel = new Label("Potencia: " + car.getHorsePower() + " CV");
        powerLabel.getStyleClass().add("car-card-text");

        Label registrationLabel = new Label("Matrícula: " + car.getRegistrationDate());
        registrationLabel.getStyleClass().add("car-card-text");

        card.getChildren().addAll(
                carImage,
                titleLabel,
                typeLabel,
                powerLabel,
                registrationLabel
        );

        return card;
    }

    private Node createCarImage(String imagePath) {
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(198, 120);
        imageContainer.getStyleClass().add("car-image-container");

        URL imageUrl = getImageUrl(imagePath);

        if (imageUrl == null) {
            Label missingImageLabel = new Label("Imagen no disponible");
            missingImageLabel.getStyleClass().add("car-image-placeholder");
            imageContainer.getChildren().add(missingImageLabel);
            return imageContainer;
        }

        Image image = new Image(imageUrl.toExternalForm());

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(190);
        imageView.setFitHeight(110);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        StackPane.setAlignment(imageView, Pos.CENTER);
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

        return CarsController.class.getResource(normalizedPath);
    }
}