package model;

public class Session {
    private static String currentUsername;
    private static String currentRole;

    public static void set(String username, String role) {
        currentUsername = username;
        currentRole = role;
    }

    public static String getUsername() {
        return currentUsername;
    }

    public static String getRole() {
        return currentRole;
    }

    public static boolean isAdmin() {
        return "admin".equals(currentRole);
    }

    public static boolean isLoggedIn() {
        return currentUsername != null;
    }

    public static void clear() {
        currentUsername = null;
        currentRole = null;
    }
}
