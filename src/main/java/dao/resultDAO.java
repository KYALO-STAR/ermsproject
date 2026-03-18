package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import models.ResultRecord;

public class resultDAO {

    /**
     * Saves or updates a student's result and script path.
     */
    public boolean saveResult(int examId, int studentId, int marks, String path) {
        String sql = "INSERT INTO results (exam_id, student_id, marks_obtained, file_path) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE marks_obtained = VALUES(marks_obtained), " +
                     "file_path = VALUES(file_path), uploaded_at = CURRENT_TIMESTAMP";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Bind parameters and execute. Using try-with-resources ensures the
            // Connection and PreparedStatement are closed automatically to avoid leaks.
            ps.setInt(1, examId);
            ps.setInt(2, studentId);
            ps.setInt(3, marks);
            ps.setString(4, path);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Keep error output simple for the assignment; in larger projects use structured logging.
            System.err.println("SQL Error in resultDAO: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fetches results for a specific student and returns a list of ResultRecord.
     * Ensures all JDBC resources are closed by using try-with-resources.
     */
    public List<ResultRecord> getStudentResults(int studentId) {
        String sql = "SELECT r.*, e.unit_name FROM results r " +
                     "JOIN exams e ON r.exam_id = e.id " +
                     "WHERE r.student_id = ?";

        List<ResultRecord> results = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Set the student id and run the query. ResultSet is closed by
            // the nested try-with-resources so no JDBC resources leak.
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ResultRecord r = new ResultRecord(
                        rs.getInt("exam_id"),
                        rs.getInt("student_id"),
                        rs.getInt("marks_obtained"),
                        rs.getString("file_path"),
                        rs.getString("unit_name")
                    );
                    results.add(r);
                }
            }
        } catch (SQLException e) {
            // Print error for debugging in the class project; replace with logger if evolving.
            e.printStackTrace();
        }

        return results;
    }
}