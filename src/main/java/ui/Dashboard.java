package ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import database.UserSession;

public class Dashboard extends Frame {
    public Dashboard() {
        if (UserSession.getCurrentUser() == null) {
            System.err.println("No user in session — redirecting to login.");
            new loginFrame();
            dispose();
            return;
        }

        String name = UserSession.getCurrentUser().getFullName();
        String role = UserSession.getCurrentUser().getRole();

        setTitle("ERMS Dashboard - " + role.toUpperCase());
        setSize(700, 520);
        setLayout(new BorderLayout(10, 10));

        Font headerFont = new Font("SansSerif", Font.BOLD, 18);
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);

        Panel topPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(new Color(34, 139, 230));
        Label lblWelcome = new Label("Welcome, " + name);
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(headerFont);
        topPanel.add(lblWelcome);
        add(topPanel, BorderLayout.NORTH);

        Panel menuPanel = new Panel(new GridLayout(0, 2, 12, 12));
        menuPanel.setBackground(Color.WHITE);

        // Admin
        if (role.equalsIgnoreCase("admin")) {
            Button btnManageUsers = new Button("Manage Users");
            btnManageUsers.setFont(buttonFont);
            btnManageUsers.addActionListener(e -> new RegistrationFrame(true));
            menuPanel.add(btnManageUsers);

            Button btnLogs = new Button("View System Logs");
            btnLogs.addActionListener(e -> new LogsFrame());
            btnLogs.setFont(buttonFont);
            menuPanel.add(btnLogs);

        // Lecturer
        } else if (role.equalsIgnoreCase("lecturer or admin")) {
            Button btnUpload = new Button("Upload Exam Marks");
            btnUpload.setFont(buttonFont);
            btnUpload.addActionListener(e -> new MarkUploadFrame());
            menuPanel.add(btnUpload);

            Button btnEditGrades = new Button("Edit Student Grades");
            btnEditGrades.setFont(buttonFont);
            menuPanel.add(btnEditGrades);

            Button btnViewClass = new Button("View Class Performance");
            btnViewClass.setFont(buttonFont);
            menuPanel.add(btnViewClass);

        // Student
        } else {
            Button btnResults = new Button("View My Results");
            btnResults.setFont(buttonFont);
            menuPanel.add(btnResults);

            Button btnTranscript = new Button("Download Transcript");
            btnTranscript.setFont(buttonFont);
            menuPanel.add(btnTranscript);
        }

        add(menuPanel, BorderLayout.CENTER);

        Panel bottom = new Panel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        Button btnLogout = new Button("Logout");
        btnLogout.setFont(buttonFont);
        btnLogout.addActionListener(e -> { UserSession.logout(); new loginFrame(); dispose(); });
        bottom.add(btnLogout);
        add(bottom, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { System.exit(0); } });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}