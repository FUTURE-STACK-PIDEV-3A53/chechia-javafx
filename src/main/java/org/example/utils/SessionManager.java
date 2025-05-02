package org.example.utils;

import org.example.model.User;

public class SessionManager {
    private static User currentUser;

    public static void setUser(User u) {
        currentUser = u;
    }

    public static User getUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void clear() {
        currentUser = null;
    }
} 