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
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JOptionPane;

import dao.resultDAO;
import database.UserSession;
import models.ResultRecord;

public class ClassPerformanceFrame extends Frame {
    private TextField txtExamId;
    private Button btnLoad, btnBack;
    private TextArea resultsDisplay;
    private Label lblStatus;

    public ClassPerformanceFrame() {
        setTitle("ERMS - Class Performance");
        setSize(600, 500);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(250, 250, 250));

        Font headerFont = new Font("SansSerif", Font.BOLD, 18);
        Font labelFont = new Font("SansSerif", Font.PLAIN, 14);
        Font inputFont = new Font("SansSerif", Font.PLAIN, 14);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 14);
        Font textFont = new Font("Monospaced", Font.PLAIN, 12);

        // Top Panel for input and load button
        Panel inputPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.add(new Label("Exam ID:", Label.RIGHT));
        txtExamId = new TextField(10);
        txtExamId.setFont(inputFont);
        inputPanel.add(txtExamId);

        btnLoad = new Button("Load Performance");
        btnLoad.setFont(buttonFont);
        btnLoad.setBackground(new Color(30, 144, 255));
        btnLoad.setForeground(Color.WHITE);
        btnLoad.addActionListener(e -> loadClassPerformance());
        inputPanel.add(btnLoad);
        add(inputPanel, BorderLayout.NORTH);

        // Results Display Area
        resultsDisplay = new TextArea();
        resultsDisplay.setEditable(false);
        resultsDisplay.setFont(textFont);
        add(resultsDisplay, BorderLayout.CENTER);

        // Bottom Panel for status and back button
        Panel bottomPanel = new Panel(new GridLayout(2, 1));
        lblStatus = new Label("", Label.CENTER);
        lblStatus.setForeground(Color.RED);
        bottomPanel.add(lblStatus);

        btnBack = new Button("Back to Dashboard");
        btnBack.setFont(buttonFont);
        btnBack.addActionListener(e -> {
            new Dashboard();
            dispose();
        });
        Panel backButtonPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        backButtonPanel.add(btnBack);
        bottomPanel.add(backButtonPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadClassPerformance() {
        lblStatus.setText("");
        resultsDisplay.setText("");

        try {
            int examId = Integer.parseInt(txtExamId.getText().trim());
            resultDAO dao = new resultDAO();
            List<ResultRecord> results = dao.getResultsForExam(examId);

            if (results.isEmpty()) {
                resultsDisplay.append("No results found for Exam ID: " + examId);
                lblStatus.setText("No results found.");
            } else {
                resultsDisplay.append(String.format("%-10s %-25s %-15s %s\n", "Std ID", "Student Name", "Unit Name", "Marks"));
                resultsDisplay.append("-------------------------------------------------------------------\n");
                for (ResultRecord result : results) {
                    resultsDisplay.append(String.format("%-10d %-25s %-15s %d\n",
                        result.getStudentId(),
                        result.getStudentName(),
                        result.getUnitName(),
                        result.getMarksObtained()
                    ));
                }
                lblStatus.setText("Performance loaded for Exam ID: " + examId);
            }

        } catch (NumberFormatException ex) {
            lblStatus.setText("Please enter a valid number for Exam ID.");
        } catch (Exception ex) {
            lblStatus.setText("Error loading class performance: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
