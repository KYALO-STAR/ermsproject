package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DBConnection;

public class resultDAO {

    /**
     * Saves or updates a student's result and script path.
     * Uses 'ON DUPLICATE KEY UPDATE' because of your UNIQUE KEY (exam_id, student_id)
     */
    public boolean saveResult(int examId, int studentId, int marks, String path) {
        // This SQL handles the "One script per student per unit" rule. 
        // If it exists, it updates the marks and path instead of crashing.
        String sql = "INSERT INTO results (exam_id, student_id, marks_obtained, file_path) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE marks_obtained = VALUES(marks_obtained), " +
                     "file_path = VALUES(file_path), uploaded_at = CURRENT_TIMESTAMP";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, examId);
            ps.setInt(2, studentId);
            ps.setInt(3, marks);
            ps.setString(4, path);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("SQL Error in resultDAO: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fetches results for a specific student to be displayed on their dashboard.
     */
    public ResultSet getStudentResults(int studentId) {
        String sql = "SELECT r.*, e.unit_name FROM results r " +
                     "JOIN exams e ON r.exam_id = e.id " +
                     "WHERE r.student_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}