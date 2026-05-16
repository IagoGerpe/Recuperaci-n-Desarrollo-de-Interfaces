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

public class UserController { //clase solo accesible para administradores (espero). Maneja la vista de gestión de usuarios

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
    private void initialize() { //método inicial, comprueba que el usuario que accede sea admin y carga la vista y su informacion
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
    private void handleFilterUsers() { //método para filtrar a los usuarios por su email
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
    private void handleClearFilter() { //método que limpia el filtro del email
        emailFilterField.clear();
        loadUsers();
    }

    @FXML
    private void handleChangeRole() { //método encargado de cambiar el rol de un usuario
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
    private void handleDeleteUser() { //método encargado de borrar un usuario de la base de datos
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

    private void configureTable() { //este método conecta cada columna de la tabla con una propiedad de los usuarios
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    private void configureRoleComboBox() { //esto crea los roles disponibles en el seletor de roles
        roleComboBox.getItems().setAll("user", "admin");
    }

    private void configureTableSelection() { //este método actualiza la combobox en caso de que cambiemos la selección de usuario
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

    private void loadUsers() { //este método carga todos los usuarios de la base de datos
        List<User> users = userDAO.findAll();
        showUsers(users);

        if (users.isEmpty()) {
            messageLabel.setText("No hay usuarios registrados.");
        } else {
            messageLabel.setText("Mostrando " + users.size() + " usuarios.");
        }
    }

    private void showUsers(List<User> users) { //este método recibe todos los usuarios en una lista y los coloca en la tabla
        ObservableList<User> observableUsers = FXCollections.observableArrayList(users);
        usersTableView.setItems(observableUsers);
    }

    private boolean isCurrentUser(User user) { //este método se utiliza para evitar que se borre o actualice el usuario que se está utilizando
        User currentUser = Session.getCurrentUser();

        return currentUser != null && currentUser.getId() == user.getId();
    }
}
