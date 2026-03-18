package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import database.UserSession;
import models.AuditLogRecord;

public class auditDAO {
    public static void log(String action, String description) {
        String sql = "INSERT INTO audit_logs (user_id, action_type, description) VALUES (?, ?, ?)";

        if (UserSession.getCurrentUser() == null) {
            System.err.println("Audit Log Skipped: no active user session.");
            return;
        }

        int userId = UserSession.getCurrentUser().getId();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, description);
            ps.executeUpdate();

        } catch (Exception e) {
            System.err.println("Audit Log Failed: " + e.getMessage());
        }
    }

    public List<AuditLogRecord> getAuditLogs() {
        String sql =
            "SELECT a.user_id, COALESCE(u.full_name, CONCAT('User #', a.user_id)) AS actor_name, " +
            "a.action_type, a.description " +
            "FROM audit_logs a " +
            "LEFT JOIN users u ON u.id = a.user_id";

        List<AuditLogRecord> logs = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                logs.add(new AuditLogRecord(
                    rs.getInt("user_id"),
                    rs.getString("actor_name"),
                    rs.getString("action_type"),
                    rs.getString("description")
                ));
            }
        } catch (Exception e) {
            System.err.println("Audit Log Read Failed: " + e.getMessage());
        }

        return logs;
    }
}