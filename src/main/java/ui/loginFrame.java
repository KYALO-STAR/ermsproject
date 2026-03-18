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
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import dao.userDAO;
import models.User;

public class loginFrame extends Frame {
    TextField txtEmail = new TextField(25);
    TextField txtPass = new TextField(25);
    Button btnLogin = new Button("Login");
    Label lblError = new Label("");

    public loginFrame() {
        setTitle("Exam Portal");
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(250, 250, 250));
        txtPass.setEchoChar('*');

        // Fonts and colors for a cleaner, modern look
        Font headerFont = new Font("SansSerif", Font.BOLD, 20);
        Font labelFont = new Font("SansSerif", Font.PLAIN, 14);
        Font inputFont = new Font("SansSerif", Font.PLAIN, 14);

        Label header = new Label("Exam Portal", Label.CENTER);
        header.setFont(headerFont);
        header.setForeground(new Color(34, 34, 34));
        add(header, BorderLayout.NORTH);

        // 1. Define the form panel (The grid for Labels and TextFields)
        Panel form = new Panel(new GridLayout(2, 2, 8, 8));
        
        Label lEmail = new Label("Email:");
        lEmail.setFont(labelFont);
        lEmail.setForeground(new Color(60, 60, 60));
        txtEmail.setFont(inputFont);
        
        Label lPass = new Label("Password:");
        lPass.setFont(labelFont);
        lPass.setForeground(new Color(60, 60, 60));
        txtPass.setFont(inputFont);

        // Add components to the grid
        form.add(lEmail); form.add(txtEmail);
        form.add(lPass); form.add(txtPass);

        Panel paddingPanel = new Panel(new GridLayout(1, 1, 0, 0));
        paddingPanel.setBackground(getBackground());
        paddingPanel.add(form);

        // 3. Add the paddingPanel to the Frame's center
        add(paddingPanel, BorderLayout.CENTER);

        Panel actions = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnLogin.setBackground(new Color(30, 144, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(labelFont);
        actions.add(btnLogin);

        Button btnSignUp = new Button("Create account");
        btnSignUp.setForeground(new Color(30, 144, 255));
        btnSignUp.setFont(labelFont);
        actions.add(btnSignUp);

        lblError.setForeground(Color.RED);
        lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        Panel errorPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        errorPanel.add(lblError);

        // Combine error message and actions into one south container so
        // BorderLayout doesn't overwrite one with the other.
        Panel south = new Panel(new GridLayout(2, 1, 0, 4));
        south.add(errorPanel);
        south.add(actions);
        add(south, BorderLayout.SOUTH);

        // Single clean login flow
        btnLogin.addActionListener(e -> handleLogin());

        btnSignUp.addActionListener(e -> {
            new RegistrationFrame(false);
            dispose();
        });

        addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { System.exit(0); } });

        setSize(420, 260);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleLogin() {
        userDAO dao = new userDAO();
        String email = txtEmail.getText().trim();
        String pass = txtPass.getText();

        User user = dao.login(email, pass);
        if (user != null) {
            System.out.println("Login Success! Welcome " + user.getFullName());
            database.UserSession.init(user);
            new Dashboard();
            this.dispose();
        } else {
            lblError.setText("Invalid credentials. Try again.");
        }
    }
}