package com.recuperacion.carmanager.controller;

import com.recuperacion.carmanager.dao.UserDAO;
import com.recuperacion.carmanager.model.User;
import com.recuperacion.carmanager.utils.Session;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class UserController {

    private final UserDAO userDAO = new UserDAO();

    private User selectedUser;

    @FXML
    private TextField emailFilterField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private TableView<User> usersTableView;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        if (!Session.isAdmin()) {
            messageLabel.setText("No tienes permisos para ver esta vista.");
            return;
        }

        configureTable();
        configureRoleComboBox();
        configureTableSelection();
        loadUsers();
    }

    @FXML
    private void handleFilterUsers() {
        String emailFilter = emailFilterField.getText().trim();

        if (emailFilter.isBlank()) {
            messageLabel.setText("Introduce un email para filtrar.");
            return;
        }

        List<User> filteredUsers = userDAO.findByEmail(emailFilter);
        showUsers(filteredUsers);

        messageLabel.setText("Filtro aplicado: " + emailFilter);
    }

    @FXML
    private void handleClearFilter() {
        emailFilterField.clear();
        loadUsers();
    }

    @FXML
    private void handleChangeRole() {
        if (selectedUser == null) {
            messageLabel.setText("Selecciona un usuario primero.");
            return;
        }

        String selectedRole = roleComboBox.getValue();

        if (selectedRole == null || selectedRole.isBlank()) {
            messageLabel.setText("Selecciona un rol.");
            return;
        }

        if (isCurrentUser(selectedUser)) {
            messageLabel.setText("No puedes cambiar tu propio rol desde esta vista.");
            return;
        }

        boolean updated = userDAO.updateRole(selectedUser.getId(), selectedRole);

        if (updated) {
            messageLabel.setText("Rol actualizado correctamente.");
            selectedUser = null;
            roleComboBox.setValue(null);
            loadUsers();
        } else {
            messageLabel.setText("No se pudo actualizar el rol.");
        }
    }

    @FXML
    private void handleDeleteUser() {
        if (selectedUser == null) {
            messageLabel.setText("Selecciona un usuario primero.");
            return;
        }

        if (isCurrentUser(selectedUser)) {
            messageLabel.setText("No puedes eliminar el usuario con el que has iniciado sesión.");
            return;
        }

        boolean deleted = userDAO.deleteById(selectedUser.getId());

        if (deleted) {
            messageLabel.setText("Usuario eliminado correctamente.");
            selectedUser = null;
            roleComboBox.setValue(null);
            loadUsers();
        } else {
            messageLabel.setText("No se pudo eliminar el usuario.");
        }
    }

    private void configureTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    private void configureRoleComboBox() {
        roleComboBox.getItems().setAll("user", "admin");
    }

    private void configureTableSelection() {
        usersTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldUser, newUser) -> {
                    selectedUser = newUser;

                    if (newUser != null) {
                        roleComboBox.setValue(newUser.getRole());
                        messageLabel.setText("Usuario seleccionado: " + newUser.getUsername());
                    }
                });
    }

    private void loadUsers() {
        List<User> users = userDAO.findAll();
        showUsers(users);

        if (users.isEmpty()) {
            messageLabel.setText("No hay usuarios registrados.");
        } else {
            messageLabel.setText("Mostrando " + users.size() + " usuarios.");
        }
    }

    private void showUsers(List<User> users) {
        ObservableList<User> observableUsers = FXCollections.observableArrayList(users);
        usersTableView.setItems(observableUsers);
    }

    private boolean isCurrentUser(User user) {
        User currentUser = Session.getCurrentUser();

        return currentUser != null && currentUser.getId() == user.getId();
    }
}
