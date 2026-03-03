package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DBConnection;
import models.User;

public class userDAO {
    
    public User login(String email, String password_hash) {
        // NOTE: Ensure  table column is 'password' or 'password_hash'
        String sql = "SELECT id, full_name, email, role FROM users WHERE email = ? AND password_hash = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ps.setString(2, password_hash);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Correctly mapping DB columns to the User constructor
                return new User(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    "", // We don't need to pass the password back to the UI
                    rs.getString("role")
                );
            }
        } catch (Exception e) {
            System.err.println("Login Error: " + e.getMessage());
        }
        return null; 
    }

    public boolean registerUser(User newUser) {
        String sql = "INSERT INTO users (full_name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newUser.getFullName());
            pstmt.setString(2, newUser.getEmail());
            pstmt.setString(3, newUser.getPassword());
            pstmt.setString(4, newUser.getRole());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("Registration Error!");
            e.printStackTrace();
            return false;
        }
    }
}