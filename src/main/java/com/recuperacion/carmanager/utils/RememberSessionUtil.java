package com.recuperacion.carmanager.utils;

import com.recuperacion.carmanager.dao.UserDAO;
import com.recuperacion.carmanager.model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RememberSessionUtil { //este método crea un txt que guarda la sesión de la ultima persona que se haya logeado

    private static final String SESSIONFOLDER = "src/main/resources/session";
    private static final String SESSIONFILE = "session.txt";

    private RememberSessionUtil() {
    }

    public static void saveRememberedUser(String username) { //este metodo crea el txt con la persona que haya iniciado sesión
        try {
            Path sessionFolderPath = getSessionFolderPath();
            Path sessionFilePath = getSessionFilePath();

            if (!Files.exists(sessionFolderPath)) {
                Files.createDirectories(sessionFolderPath);
            }

            Files.writeString(sessionFilePath, username);

        } catch (IOException exception) {
            System.out.println("Error al guardar la sesión persistente: " + exception.getMessage());
        }
    }

    public static User loadRememberedUser() { //este metodo carga el usuario guardado en el txt y salta la pantalla de login al ejecutar el programa
        Path sessionFilePath = getSessionFilePath();

        if (!Files.exists(sessionFilePath)) {
            return null;
        }

        try {
            String username = Files.readString(sessionFilePath).trim();

            if (username.isBlank()) {
                clearRememberedUser();
                return null;
            }

            UserDAO userDAO = new UserDAO();
            User user = userDAO.findByUsername(username);

            if (user == null) {
                clearRememberedUser();
                return null;
            }

            return user;

        } catch (IOException exception) {
            System.out.println("Error al leer la sesión persistente: " + exception.getMessage());
            return null;
        }
    }

    public static void clearRememberedUser() { //borra el archivo de sesión cuando desmarquemos el checkbox
        try {
            Files.deleteIfExists(getSessionFilePath());
        } catch (IOException exception) {
            System.out.println("Error al borrar la sesión persistente: " + exception.getMessage());
        }
    }

    public static Path getSessionFilePath() {
        return getSessionFolderPath().resolve(SESSIONFILE);
    }

    private static Path getSessionFolderPath() {
        return Path.of(SESSIONFOLDER);
    }
}