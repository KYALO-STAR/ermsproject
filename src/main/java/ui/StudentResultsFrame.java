package ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JOptionPane;

import dao.resultDAO;
import database.UserSession;
import models.ResultRecord;
import models.User;

public class StudentResultsFrame extends Frame {
    private TextArea resultsDisplay;
    private Button btnBack;

    public StudentResultsFrame() {
        setTitle("ERMS - My Results");
        setSize(500, 400);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(250, 250, 250));

        Font headerFont = new Font("SansSerif", Font.BOLD, 18);
        Font textFont = new Font("Monospaced", Font.PLAIN, 12);
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);

        Label header = new Label("My Exam Results", Label.CENTER);
        header.setFont(headerFont);
        add(header, BorderLayout.NORTH);

        resultsDisplay = new TextArea();
        resultsDisplay.setEditable(false);
        resultsDisplay.setFont(textFont);
        add(resultsDisplay, BorderLayout.CENTER);

        btnBack = new Button("Back to Dashboard");
        btnBack.setFont(buttonFont);
        btnBack.addActionListener(e -> {
            new Dashboard();
            dispose();
        });
        Panel buttonPanel = new Panel();
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        loadStudentResults();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadStudentResults() {
        User currentUser = UserSession.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Error: No active user session.", "Error", JOptionPane.ERROR_MESSAGE);
            new loginFrame();
            dispose();
            return;
        }

        resultsDisplay.setText(""); // Clear existing results
        resultDAO dao = new resultDAO();
        List<ResultRecord> results = dao.getStudentResults(currentUser.getId());

        if (results.isEmpty()) {
            resultsDisplay.append("No results found for you yet.");
        } else {
            resultsDisplay.append(String.format("%-25s %-10s\n", "Unit Name", "Marks"));
            resultsDisplay.append("---------------------------------------\n");
            for (ResultRecord result : results) {
                resultsDisplay.append(String.format("%-25s %-10d\n",
                    result.getUnitName(),
                    result.getMarksObtained()
                ));
            }
        }
    }
}
