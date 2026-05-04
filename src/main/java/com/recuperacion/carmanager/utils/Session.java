package com.recuperacion.carmanager.utils;

import com.recuperacion.carmanager.model.User;

import java.util.Objects;

public class Session {

    private static User currentUser;

    private Session() {
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return Objects.equals(currentUser.getRole(), "admin");
    }

    public static void clear() {
        currentUser = null;
    }
}
