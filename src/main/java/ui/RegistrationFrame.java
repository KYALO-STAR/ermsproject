package ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import dao.userDAO;
import models.User;
import dao.auditDAO;

public class RegistrationFrame extends Frame {
    TextField txtName, txtEmail, txtPass;
    Choice roleChoice;
    Button btnRegister, btnBack;

    public RegistrationFrame(boolean isAdminMode) {
        setTitle("ERMS - Register New User");
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(250, 250, 250));

        Font headerFont = new Font("SansSerif", Font.BOLD, 18);
        Font labelFont = new Font("SansSerif", Font.PLAIN, 14);
        Font inputFont = new Font("SansSerif", Font.PLAIN, 14);

        Label header = new Label("Create Account", Label.CENTER);
        header.setFont(headerFont);
        add(header, BorderLayout.NORTH);

        Panel form = new Panel(new GridLayout(4, 2, 8, 8));
        form.setBackground(getBackground());

        form.add(new Label("Full Name:"));
        txtName = new TextField(); txtName.setFont(inputFont);
        form.add(txtName);

        form.add(new Label("Email:"));
        txtEmail = new TextField(); txtEmail.setFont(inputFont);
        form.add(txtEmail);

        form.add(new Label("Password:"));
        txtPass = new TextField(); txtPass.setEchoChar('*'); txtPass.setFont(inputFont);
        form.add(txtPass);

        form.add(new Label("Role:"));
        roleChoice = new Choice();
        if (isAdminMode) {
            roleChoice.add("student"); roleChoice.add("lecturer"); roleChoice.add("admin");
        } else {
            roleChoice.add("student");
        }
        form.add(roleChoice);

        Panel center = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        center.add(form);
        add(center, BorderLayout.CENTER);

        btnRegister = new Button("Create Account");
        btnRegister.setBackground(new Color(30, 144, 255));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(labelFont);

        btnBack = new Button("Cancel");
        btnBack.setFont(labelFont);

        Panel actions = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        actions.add(btnRegister); actions.add(btnBack);
        add(actions, BorderLayout.SOUTH);

        btnRegister.addActionListener(e -> {
            String assignedRole = isAdminMode ? roleChoice.getSelectedItem() : "student";
            User newUser = new User(0, txtName.getText().trim(), txtEmail.getText().trim(), txtPass.getText(), assignedRole);
            userDAO dao = new userDAO();
            if (dao.registerUser(newUser)) {
                System.out.println("Account created successfully!");
                new loginFrame();
                dispose();
            }// Inside RegistrationFrame handleRegister() method
            if (dao.registerUser(newUser)) {
    // LOG THE ACTION
            auditDAO.log("USER_MANAGEMENT", "Admin added new lecturer: " + txtName.getText());
    
           javax.swing.JOptionPane.showMessageDialog(this, "Lecturer Added Successfully!");
            dispose(); // Close form on success
            } else {
                System.err.println("Failed to create account.");
                javax.swing.JOptionPane.showMessageDialog(this, "Error: Could not create account. Please try again.", "Registration Failed", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });

        btnBack.addActionListener(e -> { new loginFrame(); dispose(); });

        addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { dispose(); } });

        setSize(420, 360);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}