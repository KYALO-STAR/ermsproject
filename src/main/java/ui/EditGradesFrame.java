package ui;

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
import java.util.List;

import javax.swing.JOptionPane;

import dao.resultDAO;
import database.UserSession;
import models.ResultRecord;

public class EditGradesFrame extends Frame {
    private TextField txtExamId, txtStudentId, txtMarks;
    private Label lblCurrentMarks, lblStatus;
    private Button btnLoad, btnSave, btnBack;

    private ResultRecord currentResult = null; // To hold the loaded result for editing

    public EditGradesFrame() {
        setTitle("ERMS - Edit Student Grades");
        setSize(500, 300);
        setLayout(new GridLayout(6, 2, 10, 10));
        setBackground(new Color(250, 250, 250));

        Font labelFont = new Font("SansSerif", Font.PLAIN, 14);
        Font inputFont = new Font("SansSerif", Font.PLAIN, 14);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 14);

        // Input Fields
        add(new Label("Exam ID (Unit Code):", Label.RIGHT));
        txtExamId = new TextField(10);
        txtExamId.setFont(inputFont);
        add(txtExamId);

        add(new Label("Student ID:", Label.RIGHT));
        txtStudentId = new TextField(10);
        txtStudentId.setFont(inputFont);
        add(txtStudentId);

        btnLoad = new Button("Load Grades");
        btnLoad.setFont(buttonFont);
        btnLoad.setBackground(new Color(30, 144, 255));
        btnLoad.setForeground(Color.WHITE);
        btnLoad.addActionListener(e -> loadStudentGrade());
        add(new Label("")); // Spacer
        add(btnLoad);

        add(new Label("Current Marks:", Label.RIGHT));
        lblCurrentMarks = new Label("N/A", Label.LEFT);
        lblCurrentMarks.setFont(labelFont);
        add(lblCurrentMarks);

        add(new Label("New Marks:", Label.RIGHT));
        txtMarks = new TextField(10);
        txtMarks.setFont(inputFont);
        add(txtMarks);

        // Action Buttons
        btnSave = new Button("Save Changes");
        btnSave.setFont(buttonFont);
        btnSave.setBackground(new Color(50, 205, 50));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> saveStudentGrade());
        btnSave.setEnabled(false); // Disable until a grade is loaded

        btnBack = new Button("Back to Dashboard");
        btnBack.setFont(buttonFont);
        btnBack.addActionListener(e -> {
            new Dashboard();
            dispose();
        });

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(btnSave);
        buttonPanel.add(btnBack);
        add(new Label("")); // Spacer
        add(buttonPanel);
        
        lblStatus = new Label("", Label.CENTER);
        lblStatus.setForeground(Color.RED);
        add(lblStatus);


        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadStudentGrade() {
        lblStatus.setText("");
        try {
            int examId = Integer.parseInt(txtExamId.getText().trim());
            int studentId = Integer.parseInt(txtStudentId.getText().trim());

            resultDAO dao = new resultDAO();
            // resultDAO.getStudentResults fetches ALL results for a student.
            // We need to find the specific one for the given examId.
            List<ResultRecord> studentResults = dao.getStudentResults(studentId);
            
            currentResult = null;
            for (ResultRecord rr : studentResults) {
                if (rr.getExamId() == examId) {
                    currentResult = rr;
                    break;
                }
            }

            if (currentResult != null) {
                lblCurrentMarks.setText(String.valueOf(currentResult.getMarksObtained()));
                txtMarks.setText(String.valueOf(currentResult.getMarksObtained())); // Pre-fill new marks with current
                btnSave.setEnabled(true);
                lblStatus.setText("Grade loaded. Make changes and save.");
            } else {
                lblCurrentMarks.setText("N/A");
                txtMarks.setText("");
                btnSave.setEnabled(false);
                lblStatus.setText("No result found for this Exam ID and Student ID.");
            }

        } catch (NumberFormatException ex) {
            lblStatus.setText("Please enter valid numbers for Exam ID and Student ID.");
            btnSave.setEnabled(false);
        } catch (Exception ex) {
            lblStatus.setText("Error loading grade: " + ex.getMessage());
            btnSave.setEnabled(false);
            ex.printStackTrace();
        }
    }

    private void saveStudentGrade() {
        lblStatus.setText("");
        if (currentResult == null) {
            lblStatus.setText("No grade loaded to save.");
            return;
        }

        try {
            int newMarks = Integer.parseInt(txtMarks.getText().trim());
            
            resultDAO dao = new resultDAO();
            if (dao.saveResult(currentResult.getExamId(), currentResult.getStudentId(), newMarks, currentResult.getFilePath())) {
                JOptionPane.showMessageDialog(this, "Grade updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                lblStatus.setText("Grade updated.");
                currentResult = new ResultRecord(currentResult.getExamId(), currentResult.getStudentId(), newMarks, currentResult.getFilePath(), currentResult.getUnitName(), currentResult.getStudentName());
                lblCurrentMarks.setText(String.valueOf(newMarks)); // Update displayed current marks
            } else {
                lblStatus.setText("Failed to update grade.");
                JOptionPane.showMessageDialog(this, "Failed to update grade.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            lblStatus.setText("Please enter a valid number for New Marks.");
        } catch (Exception ex) {
            lblStatus.setText("Error saving grade: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}