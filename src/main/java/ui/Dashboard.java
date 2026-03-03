package ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import database.UserSession;

public class Dashboard extends Frame {
    public Dashboard() {
        // Get the logged-in user's info from the Session we set during login
        // guard against missing session
        if (UserSession.getCurrentUser() == null) {
            System.err.println("No user in session — redirecting to login.");
            new loginFrame();
            dispose();
            return;
        }
        String name = UserSession.getCurrentUser().getFullName();
        String role = UserSession.getCurrentUser().getRole();

        setTitle("ERMS Dashboard - " + role.toUpperCase());
        setSize(600, 500);
        setLayout(new BorderLayout());
        
        // TOP PANEL: Welcome Message
        Panel topPanel = new Panel();
        topPanel.setBackground(new Color(51, 153, 255));
        Label lblWelcome = new Label("Welcome, " + name, Label.CENTER);
        lblWelcome.setForeground(Color.WHITE);
        topPanel.add(lblWelcome);
        add(topPanel, BorderLayout.NORTH);

        // CENTER PANEL: Role-Specific Buttons
        Panel menuPanel = new Panel(new GridLayout(4, 1, 10, 10));
        
        if (role.equalsIgnoreCase("admin")) {
            Button btnManageUsers = new Button("Manage Users (Add/Edit)");
            btnManageUsers.addActionListener(e -> new RegistrationFrame(true));
            menuPanel.add(btnManageUsers);
            
            menuPanel.add(new Button("View System Logs"));
        } else if (role.equalsIgnoreCase("lecturer")) {
            menuPanel.add(new Button("Upload Exam Marks"));
            menuPanel.add(new Button("Edit Student Grades"));
        } else {
            menuPanel.add(new Button("View My Results"));
            menuPanel.add(new Button("Download Transcript"));
        }


        // Inside your Dashboard.java center panel logic:
if (role.equalsIgnoreCase("lecturer")) {
    Button btnUpload = new Button("Upload Student Marks (CSV)");
    Button btnViewClass = new Button("View Class Performance");

    btnUpload.addActionListener(e -> {
        // This is where the Scanner logic will live!
        new MarkUploadFrame(); 
    });

    menuPanel.add(btnUpload);
    menuPanel.add(btnViewClass);
}

        add(menuPanel, BorderLayout.CENTER);

        // BOTTOM PANEL: Logout
        Button btnLogout = new Button("Logout");
        btnLogout.addActionListener(e -> {
            UserSession.logout();
            new loginFrame();
            dispose();
        });
        add(btnLogout, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}