package com.recuperacion.carmanager.utils;

import com.recuperacion.carmanager.dao.UserDAO;
import com.recuperacion.carmanager.model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RememberSessionUtil {

    private static final String SESSIONFOLDER = "src/main/resources/session";
    private static final String SESSIONFILE = "session.txt";

    private RememberSessionUtil() {
    }

    public static void saveRememberedUser(String username) {
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

    public static User loadRememberedUser() {
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

    public static void clearRememberedUser() {
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