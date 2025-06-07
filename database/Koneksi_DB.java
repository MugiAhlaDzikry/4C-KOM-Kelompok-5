package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi_DB {
    private static final String URL = "jdbc:mysql://localhost:3306/db_ayamgoreng";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            System.out.println("Connection Error: " + ex.getMessage());
            return null;
        }
    }
}
