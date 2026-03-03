package ui;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import dao.userDAO;
import models.User;

public class RegistrationFrame extends Frame {
    TextField txtName, txtEmail, txtPass;
    Choice roleChoice;
    Button btnRegister, btnBack;

    public RegistrationFrame(boolean isAdminMode) {
        setTitle("ERMS - Register New User");
        setSize(400, 450);
        setLayout(new GridLayout(6, 2, 10, 10)); // Organized grid layout
        setBackground(new Color(245, 245, 245));

        // Adding Form Components
        add(new Label("Full Name:"));
        txtName = new TextField();
        add(txtName);

        add(new Label("Email:"));
        txtEmail = new TextField();
        add(txtEmail);

        add(new Label("Password:"));
        txtPass = new TextField();
        txtPass.setEchoChar('*'); // Password masking
        add(txtPass);

        add(new Label("Role:"));
        roleChoice = new Choice();
        roleChoice = new Choice();
        
        if (isAdminMode) {
            // Admin can create anyone
            roleChoice.add("student");
            roleChoice.add("lecturer");
            roleChoice.add("admin");
        } else {
            // Public sign-up is ONLY for students
            roleChoice.add("student");
            
        }
        add(roleChoice);

        btnRegister = new Button("Create Account");
        btnBack = new Button("Cancel");

        add(btnRegister);
        add(btnBack);

        // --- Logic: What happens when you click 'Create Account' ---
       btnRegister.addActionListener(e -> {
    // If  NOT in admin mode,  force the role to "student" 
    // just in case someone tries to be clever.
       String assignedRole = isAdminMode ? roleChoice.getSelectedItem() : "student";

       User newUser = new User(0, txtName.getText(), txtEmail.getText(), txtPass.getText(), assignedRole);
       userDAO dao = new userDAO();
    
        if (dao.registerUser(newUser)) {
        System.out.println("Student account created successfully!");
        new loginFrame();
        dispose();
    }
});

        btnBack.addActionListener(e -> {
            new loginFrame();
            dispose();
        });

        // Close window behavior
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}