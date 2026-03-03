package database;

import models.User;

public class UserSession {
    // This variable holds the logged-in user's info in RAM
    private static User currentUser;

    // Call this in LoginFrame when login is successful
    public static void init(User user) {
        currentUser = user;
    }

    // Call this in Dashboard to get the user's name or role
    public static User getCurrentUser() {
        return currentUser;
    }

    // Call this when the user clicks 'Logout'
    public static void logout() {
        currentUser = null;
    }
}