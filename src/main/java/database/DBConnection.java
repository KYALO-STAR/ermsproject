package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // This method is called by your DAOs whenever they need to talk to Aiven
    public static Connection getConnection() throws SQLException {
        try {
            // 1. Get credentials from our "Secret" ConfigLoader
            String url = configLoader.get("db.url");
            String user = configLoader.get("db.user");
            String pass = configLoader.get("db.password");

            // 2. Return the live connection
            return DriverManager.getConnection(url, user, pass);
            
        } catch (Exception e) {
            System.err.println("Database Connection Failed!");
            e.printStackTrace();
            throw new SQLException("Could not connect to Aiven.");
        }
    }
}