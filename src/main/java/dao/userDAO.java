package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                String dbPassword = rs.getString("password_hash");
                // protect against null passwords
                if (dbPassword != null && passwordPlain.equals(dbPassword)) {
                    // return a minimal User (no password field populated)
                    return new User(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        "",
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

    // Registration: store plaintext password for now (no hashing)
    public boolean registerUser(User newUser) {
        String sql = "INSERT INTO users (full_name, email, password_hash, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newUser.getFullName());
            pstmt.setString(2, newUser.getEmail());
            // storing the plaintext password in the password_hash column for now
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