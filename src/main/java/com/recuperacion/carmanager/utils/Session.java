package com.recuperacion.carmanager.utils;

import com.recuperacion.carmanager.model.User;

public class Session { //método sencillo que crea una sesión

    private static User currentUser;

    private Session() {
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isAdmin() {
        return currentUser != null
                && currentUser.getRole() != null
                && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    public static void clear() {
        currentUser = null;
    }
}
