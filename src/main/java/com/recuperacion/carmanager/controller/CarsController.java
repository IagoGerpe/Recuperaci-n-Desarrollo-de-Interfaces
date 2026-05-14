package com.recuperacion.carmanager.controller;

import com.recuperacion.carmanager.dao.CarDAO;
import com.recuperacion.carmanager.model.Car;
import com.recuperacion.carmanager.model.User;
import com.recuperacion.carmanager.utils.Session;
import com.recuperacion.carmanager.dao.FavoritoDAO;

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
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.control.TitledPane;

import java.net.URL;
import java.util.List;
import java.time.LocalDate;

public class CarsController {

    private final CarDAO carDAO = new CarDAO();
    private final FavoritoDAO favoriteDAO = new FavoritoDAO();

    private Car selectedCar;
    private int currentUserFavoriteCarId = -1;
    private int mostFavoriteCarId = -1;

    @FXML
    private TitledPane adminPanel;

    @FXML
    private TextField brandField;

    @FXML
    private TextField modelField;

    @FXML
    private TextField horsePowerField;

    @FXML
    private TextField carTypeField;

    @FXML
    private DatePicker registrationDatePicker;

    @FXML
    private TextField imagePathField;

    @FXML
    private Button saveCarButton;

    @FXML
    private Button cancelEditButton;

    @FXML
    private ComboBox<String> typeFilterComboBox;

    @FXML
    private TilePane carsTilePane;

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        configureAdminControls();
        loadCarTypes();
        loadCars();
    }

    @FXML
    private void handleSaveCar() {
        if (!Session.isAdmin()) {
            messageLabel.setText("No tienes permisos para modificar coches.");
            return;
        }

        if (!isCarFormValid()) {
            return;
        }

        String brand = brandField.getText().trim();
        String model = modelField.getText().trim();
        int horsePower = Integer.parseInt(horsePowerField.getText().trim());
        String carType = carTypeField.getText().trim();
        LocalDate registrationDate = registrationDatePicker.getValue();
        String imagePath = imagePathField.getText().trim();

        boolean success;

        if (selectedCar == null) {
            Car newCar = new Car(
                    brand,
                    model,
                    horsePower,
                    carType,
                    registrationDate,
                    imagePath
            );

            success = carDAO.save(newCar);

            if (success) {
                messageLabel.setText("Coche creado correctamente.");
            } else {
                messageLabel.setText("No se pudo crear el coche.");
            }

        } else {
            selectedCar.setBrand(brand);
            selectedCar.setModel(model);
            selectedCar.setHorsePower(horsePower);
            selectedCar.setCarType(carType);
            selectedCar.setRegistrationDate(registrationDate);
            selectedCar.setImagePath(imagePath);

            success = carDAO.update(selectedCar);

            if (success) {
                messageLabel.setText("Coche actualizado correctamente.");
            } else {
                messageLabel.setText("No se pudo actualizar el coche.");
            }
        }

        if (success) {
            clearCarForm();
            loadCarTypes();
            loadCars();
        }
    }

    @FXML
    private void handleCancelEdit() {
        clearCarForm();
        messageLabel.setText("Edición cancelada.");
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

    private void configureAdminControls() {
        boolean isAdmin = Session.isAdmin();

        adminPanel.setVisible(isAdmin);
        adminPanel.setManaged(isAdmin);
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
        refreshFavoriteInformation();
        carsTilePane.getChildren().clear();

        for (Car car : cars) {
            VBox card = createCarCard(car);
            carsTilePane.getChildren().add(card);
        }
    }

    private void refreshFavoriteInformation() {
        User currentUser = Session.getCurrentUser();

        if (currentUser != null) {
            currentUserFavoriteCarId = favoriteDAO.findFavoriteCarIdByUserId(currentUser.getId());
        } else {
            currentUserFavoriteCarId = -1;
        }

        mostFavoriteCarId = favoriteDAO.findMostFavoriteCarId();
    }

    private VBox createCarCard(Car car) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.setPadding(new Insets(16));
        card.setPrefWidth(230);
        card.setMinHeight(340);
        card.getStyleClass().add("car-card");

        boolean isUserFavorite = car.getId() == currentUserFavoriteCarId;
        boolean isMostFavorite = car.getId() == mostFavoriteCarId;

        if (isUserFavorite && isMostFavorite) {
            card.getStyleClass().add("favorite-and-most-card");
        } else if (isUserFavorite) {
            card.getStyleClass().add("favorite-car-card");
        } else if (isMostFavorite) {
            card.getStyleClass().add("most-favorite-car-card");
        }

        Node carImage = createCarImage(car.getImagePath());

        Label titleLabel = new Label(car.getFullName());
        titleLabel.getStyleClass().add("car-card-title");

        Label typeLabel = new Label("Tipo: " + car.getCarType());
        typeLabel.getStyleClass().add("car-card-text");

        Label powerLabel = new Label("Potencia: " + car.getHorsePower() + " CV");
        powerLabel.getStyleClass().add("car-card-text");

        Label registrationLabel = new Label("Matrícula: " + car.getRegistrationDate());
        registrationLabel.getStyleClass().add("car-card-text");

        HBox badgesBox = createFavoriteIcons(car, isUserFavorite, isMostFavorite);

        Button favoriteButton = createFavoriteButton(car, isUserFavorite);

        card.getChildren().addAll(
                carImage,
                titleLabel,
                badgesBox,
                typeLabel,
                powerLabel,
                registrationLabel,
                favoriteButton
        );

        if (Session.isAdmin()) {
            HBox adminButtons = createAdminButtons(car);
            card.getChildren().add(adminButtons);
        }

        return card;
    }

    private HBox createAdminButtons(Car car) {
        Button editButton = new Button("Editar");
        editButton.getStyleClass().add("small-secondary-button");
        editButton.setOnAction(event -> loadCarInForm(car));

        Button deleteButton = new Button("Eliminar");
        deleteButton.getStyleClass().add("small-danger-button");
        deleteButton.setOnAction(event -> deleteCar(car));

        HBox buttonsBox = new HBox(8);
        buttonsBox.setAlignment(Pos.CENTER_LEFT);
        buttonsBox.getChildren().addAll(editButton, deleteButton);

        return buttonsBox;
    }

    private void loadCarInForm(Car car) {
        if (!Session.isAdmin()) {
            messageLabel.setText("No tienes permisos para editar coches.");
            return;
        }

        selectedCar = car;

        brandField.setText(car.getBrand());
        modelField.setText(car.getModel());
        horsePowerField.setText(String.valueOf(car.getHorsePower()));
        carTypeField.setText(car.getCarType());
        registrationDatePicker.setValue(car.getRegistrationDate());
        imagePathField.setText(car.getImagePath());

        saveCarButton.setText("Actualizar coche");
        cancelEditButton.setVisible(true);
        cancelEditButton.setManaged(true);
        adminPanel.setExpanded(true);

        messageLabel.setText("Editando: " + car.getFullName());
    }

    private void deleteCar(Car car) {
        if (!Session.isAdmin()) {
            messageLabel.setText("No tienes permisos para eliminar coches.");
            return;
        }

        boolean deleted = carDAO.deleteById(car.getId());

        if (deleted) {
            messageLabel.setText("Coche eliminado correctamente.");
            clearCarForm();
            loadCarTypes();
            loadCars();
        } else {
            messageLabel.setText("No se pudo eliminar el coche.");
        }
    }

    private boolean isCarFormValid() {
        if (brandField.getText().trim().isBlank()
                || modelField.getText().trim().isBlank()
                || horsePowerField.getText().trim().isBlank()
                || carTypeField.getText().trim().isBlank()
                || registrationDatePicker.getValue() == null
                || imagePathField.getText().trim().isBlank()) {

            messageLabel.setText("Todos los campos del coche son obligatorios.");
            return false;
        }

        try {
            int horsePower = Integer.parseInt(horsePowerField.getText().trim());

            if (horsePower <= 0) {
                messageLabel.setText("La potencia debe ser mayor que cero.");
                return false;
            }

        } catch (NumberFormatException exception) {
            messageLabel.setText("La potencia debe ser un número entero.");
            return false;
        }

        return true;
    }

    private void clearCarForm() {
        selectedCar = null;

        brandField.clear();
        modelField.clear();
        horsePowerField.clear();
        carTypeField.clear();
        registrationDatePicker.setValue(null);
        imagePathField.clear();

        saveCarButton.setText("Crear coche");
        cancelEditButton.setVisible(false);
        cancelEditButton.setManaged(false);

        adminPanel.setExpanded(false);
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

    private HBox createFavoriteIcons(Car car, boolean isUserFavorite, boolean isMostFavorite) {
        HBox badgesBox = new HBox(6);
        badgesBox.setAlignment(Pos.CENTER_LEFT);

        if (isUserFavorite) {
            Label userFavoriteLabel = new Label("Tu favorito");
            userFavoriteLabel.getStyleClass().add("favorite-icon");
            badgesBox.getChildren().add(userFavoriteLabel);
        }

        if (isMostFavorite) {
            int totalFavorites = favoriteDAO.countFavoritesByCarId(car.getId());

            Label mostFavoriteLabel = new Label("Más elegido (" + totalFavorites + ")");
            mostFavoriteLabel.getStyleClass().add("most-favorite-icon");
            badgesBox.getChildren().add(mostFavoriteLabel);
        }

        return badgesBox;
    }

    private Button createFavoriteButton(Car car, boolean isUserFavorite) {
        Button favoriteButton = new Button();

        if (isUserFavorite) {
            favoriteButton.setText("Favorito actual");
            favoriteButton.setDisable(true);
            favoriteButton.getStyleClass().add("favorite-current-button");
        } else {
            favoriteButton.setText("Marcar favorito");
            favoriteButton.getStyleClass().add("favorite-button");
            favoriteButton.setOnAction(event -> markCarAsFavorite(car));
        }

        favoriteButton.setMaxWidth(Double.MAX_VALUE);

        return favoriteButton;
    }

    private void markCarAsFavorite(Car car) {
        User currentUser = Session.getCurrentUser();

        if (currentUser == null) {
            messageLabel.setText("Debes iniciar sesión para marcar favoritos.");
            return;
        }

        boolean updated = favoriteDAO.setFavoriteCar(currentUser.getId(), car.getId());

        if (updated) {
            messageLabel.setText("Has marcado como favorito: " + car.getFullName());
            loadCars();
        } else {
            messageLabel.setText("No se pudo marcar el coche como favorito.");
        }
    }
}