package dao;

import database.DBConnection;
import models.User;
import java.sql.*;

public class userDAO {
    
    public User login(String email, String password) {
        String sql = "SELECT id, email, role, full_name FROM users WHERE email = ? AND password_hash = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Create a User object from the database result
                return new User(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getString("full_name")
                );
            }
        } catch (Exception e) {
            System.err.println("Login Error: " + e.getMessage());
        }
        return null; // Return null if login fails
    }
}