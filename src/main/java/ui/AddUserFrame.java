package ui;

import dao.userDAO;
import models.User;
import java.awt.*;
import java.awt.event.*;

public class AddUserFrame extends Frame {
    TextField txtName, txtEmail, txtPass;
    Choice roleChoice;
    Button btnSave;

    public AddUserFrame() {
        setTitle("Admin - Add New User");
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new Label("Full Name:"));
        txtName = new TextField();
        add(txtName);

        add(new Label("Email:"));
        txtEmail = new TextField();
        add(txtEmail);

        add(new Label("Password:"));
        txtPass = new TextField();
        txtPass.setEchoChar('*');
        add(txtPass);

        add(new Label("Role:"));
        roleChoice = new Choice();
        roleChoice.add("student");
        roleChoice.add("lecturer");
        roleChoice.add("admin");
        add(roleChoice);

        btnSave = new Button("Register User");
        add(btnSave);

        btnSave.addActionListener(e -> handleRegistration());

        // Close window logic
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });

        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleRegistration() {
        User u = new User(0, txtName.getText(), txtEmail.getText(), txtPass.getText(), roleChoice.getSelectedItem());
        userDAO dao = new userDAO();
        
        if (dao.registerUser(u)) {
            System.out.println("User added successfully!");
            dispose(); // Close form on success
        } else {
            System.err.println("Failed to add user.");
        }
    }
}