package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt; // Import BCrypt

import database.DBConnection;
import models.User;

public class userDAO {

    // Login: lookup user by email, retrieve the stored bcrypt hash, and verify it.
    // Password comparison is done with BCrypt.checkpw(plain, hash).
    // Returns a User on success, null on failure.
    public User login(String email, String passwordPlain) {
        String sql = "SELECT id, full_name, email, password_hash, role FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String dbPasswordHash = rs.getString("password_hash");
                // protect against null passwords and use BCrypt to check
                if (dbPasswordHash != null && BCrypt.checkpw(passwordPlain, dbPasswordHash)) {
                    // return a minimal User (no password field populated)
                    return new User(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        "", // Password not returned
                        rs.getString("role")
                    );
                }
            }
        } catch (Exception e) {
            // Keep logging simple for the class project; in production use a logger.
            System.err.println("Login Error: " + e.getMessage());
        }
        return null;
    }

    // Registration: hash password with BCrypt before storing
    public boolean registerUser(User newUser) {
        String sql = "INSERT INTO users (full_name, email, password_hash, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());

            pstmt.setString(1, newUser.getFullName());
            pstmt.setString(2, newUser.getEmail());
            pstmt.setString(3, hashedPassword); // Store hashed password
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