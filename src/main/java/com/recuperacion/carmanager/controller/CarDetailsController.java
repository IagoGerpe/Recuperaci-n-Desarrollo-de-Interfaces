package com.recuperacion.carmanager.controller;

import com.recuperacion.carmanager.dao.FavoritoDAO;
import com.recuperacion.carmanager.model.Car;
import com.recuperacion.carmanager.model.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;

public class CarDetailsController { //clase que controla la vista de los detalles de cada coche individual

    private final FavoritoDAO favoritoDAO = new FavoritoDAO();

    private Car car;

    @FXML
    private BorderPane detailRoot;

    @FXML
    private ImageView carImageView;

    @FXML
    private Label imagePlaceholderLabel;

    @FXML
    private Label carNameLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label powerLabel;

    @FXML
    private Label registrationDateLabel;

    @FXML
    private Label imagePathLabel;

    @FXML
    private Label votesLabel;

    @FXML
    private Label rankingLabel;

    @FXML
    private VBox favoriteUsersBox;

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() { //método inicial para configurar la vista
        carImageView.setFitWidth(780);
        carImageView.setFitHeight(520);
        carImageView.setPreserveRatio(true);
        carImageView.setSmooth(true);
    }

    public void setCar(Car car) { //método que carga el coche en concreto que queremos mostrar
        this.car = car;
        loadCarDetails();
    }

    @FXML
    private void handleBackToCars() { //método que nos devuelve a la pestaña de carsview
        try {
            URL fxmlUrl = CarDetailsController.class.getResource("/fxml/cars-view.fxml");

            if (fxmlUrl == null) {
                throw new IllegalStateException("No se encontró la vista de coches.");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent carsView = loader.load();

            if (detailRoot.getParent() instanceof StackPane) {
                StackPane contentPane = (StackPane) detailRoot.getParent();
                contentPane.getChildren().setAll(carsView);
            }

        } catch (Exception exception) {
            messageLabel.setText("No se pudo volver a la vista de coches.");
            System.out.println("Error al volver a los coches: " + exception.getMessage());
        }
    }

    private void loadCarDetails() { //método que carga todos los detalles del coche y crea una interfaz para él
        if (car == null) {
            messageLabel.setText("No se pudo cargar el coche seleccionado.");
            return;
        }

        carNameLabel.setText(car.getFullName());
        typeLabel.setText("Tipo: " + car.getCarType());
        powerLabel.setText("Potencia: " + car.getHorsePower() + " CV");
        registrationDateLabel.setText("Fecha de matriculacion: " + car.getRegistrationDate());
        imagePathLabel.setText("Ruta de imagen: " + car.getImagePath());

        loadImage();
        loadFavoriteData();
    }

    private void loadImage() { //crea la imagen de la ventana de detalles
        URL imageUrl = getImageUrl(car.getImagePath());

        if (imageUrl == null) {
            carImageView.setVisible(false);
            imagePlaceholderLabel.setVisible(true);
            imagePlaceholderLabel.setText("Imagen no disponible");
            return;
        }

        Image image = new Image(imageUrl.toExternalForm());
        carImageView.setImage(image);
        carImageView.setVisible(true);
        imagePlaceholderLabel.setVisible(false);
    }

    private void loadFavoriteData() { //método que carga toda la información de favoritos
        int votes = favoritoDAO.countFavoritesByCarId(car.getId());
        int rankingPosition = favoritoDAO.findRankingPositionByCarId(car.getId());

        votesLabel.setText("Nº votos: " + votes);

        if (rankingPosition == -1) {
            rankingLabel.setText("Posición en el ranking: N/A");
        } else {
            rankingLabel.setText("Posición en el ranking: #" + rankingPosition);
        }

        loadFavoriteUsers();
    }

    private void loadFavoriteUsers() { //método que carga a los usuarios que tengan como favorito el coche seleccionado
        favoriteUsersBox.getChildren().clear();

        List<User> users = favoritoDAO.findUsersByFavoriteCarId(car.getId());

        if (users.isEmpty()) {
            Label emptyLabel = new Label("A nadie le gusta este coche.");
            emptyLabel.getStyleClass().add("detail-user-empty-label");
            favoriteUsersBox.getChildren().add(emptyLabel);
            return;
        }

        for (User user : users) {
            Label userLabel = new Label(user.getUsername() + " · " + user.getEmail());
            userLabel.getStyleClass().add("detail-user-label");
            favoriteUsersBox.getChildren().add(userLabel);
        }
    }

    private URL getImageUrl(String imagePath) { //método que convierte el enlace de la imagen a un formato legible por fxml
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        String normalizedPath = imagePath.startsWith("/")
                ? imagePath
                : "/" + imagePath;

        return CarDetailsController.class.getResource(normalizedPath);
    }
}
