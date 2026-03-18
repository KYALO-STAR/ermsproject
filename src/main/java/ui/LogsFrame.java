package ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import dao.auditDAO;
import models.AuditLogRecord;

public class LogsFrame extends Frame {
    private TextArea logDisplay;
    private Button btnRefresh, btnBack;

    public LogsFrame() {
        setTitle("Admin - Audit Logs");
        setSize(800, 600);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(250, 250, 250));

        Font headerFont = new Font("SansSerif", Font.BOLD, 18);
        Font textFont = new Font("Monospaced", Font.PLAIN, 12);
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);

        // Header
        // Label header = new Label("System Audit Logs", Label.CENTER);
        // header.setFont(headerFont);
        // add(header, BorderLayout.NORTH);

        // Log Display Area
        logDisplay = new TextArea();
        logDisplay.setEditable(false);
        logDisplay.setFont(textFont);
        add(logDisplay, BorderLayout.CENTER);

        // Buttons Panel
        Panel buttonPanel = new Panel();
        btnRefresh = new Button("Refresh Logs");
        btnRefresh.setFont(buttonFont);
        btnRefresh.setBackground(new Color(30, 144, 255));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> loadLogs());
        buttonPanel.add(btnRefresh);

        btnBack = new Button("Back to Dashboard");
        btnBack.setFont(buttonFont);
        btnBack.addActionListener(e -> {
            new Dashboard();
            dispose();
        });
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initial load of logs
        loadLogs();

        // Window closing event
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadLogs() {
        logDisplay.setText(""); // Clear existing logs
        auditDAO dao = new auditDAO();
        List<AuditLogRecord> logs = dao.getAuditLogs();

        if (logs.isEmpty()) {
            logDisplay.append("No audit logs found.");
        } else {
            // Add a simple header for the log display
            logDisplay.append(String.format("%-10s %-20s %-20s %s\n", "User ID", "Actor Name", "Action Type", "Description"));
            logDisplay.append("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
            for (AuditLogRecord log : logs) {
                logDisplay.append(String.format("%-10d %-20s %-20s %s\n",
                    log.getUserId(),
                    log.getActorName(),
                    log.getActionType(),
                    log.getDescription()
                ));
            }
        }
    }
}