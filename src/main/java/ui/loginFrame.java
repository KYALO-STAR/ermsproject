package ui;

import java.awt.Button;
import java.awt.Color;
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
        setLayout(new GridLayout(4, 1, 10, 10)); // Simple grid layout
        txtPass.setEchoChar('*'); 

        Panel p1 = new Panel(); p1.add(new Label("Email: ")); p1.add(txtEmail);
        Panel p2 = new Panel(); p2.add(new Label("Pass:  ")); p2.add(txtPass);
        Panel p3 = new Panel(); p3.add(btnLogin);
        
        add(p1); add(p2); add(p3); add(lblError);

        btnLogin.addActionListener(e -> {
            userDAO dao = new userDAO();
            User user = dao.login(txtEmail.getText(), txtPass.getText());
            
            if (user != null) {
                System.out.println("Login Success! Welcome " + user.getFullName());
                // Success logic: Open Dashboard here later
            } else {
                lblError.setText("Invalid credentials. Try again.");
                lblError.setForeground(Color.RED);
            }
            // Inside LoginFrame handleLogin logic:
            user = dao.login(txtEmail.getText(), txtPass.getText());
                if (
              user != null) {
              database.UserSession.init(user); // Store the user so the Dashboard knows who they are
              new Dashboard();                // Launch the Dashboard
              this.dispose();                 // Close the Login screen
          } else {

                lblError.setText("Invalid credentials. Try again.");
                lblError.setForeground(Color.RED);
          }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });

        // Inside your LoginFrame constructor
        Button btnSignUp = new Button("No account? Sign Up here");
        btnSignUp.setForeground(Color.BLUE); // Make it look like a link

        btnSignUp.addActionListener(e -> {
        new RegistrationFrame(false); // Open the registration window you just made
        dispose();               // Close the login window
      });

add(btnSignUp);

        setSize(400, 250);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}