package ui;

import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JOptionPane;

import dao.resultDAO;
import database.UserSession;
import models.ResultRecord;
import models.User;
import utils.ReportGenerator; // Import ReportGenerator

public class TranscriptFrame extends Frame {
    private Label lblStatus;
    private Button btnGenerate, btnBack;

    public TranscriptFrame() {
        setTitle("ERMS - Download Transcript");
        setSize(450, 200);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setBackground(new Color(250, 250, 250));

        Font headerFont = new Font("SansSerif", Font.BOLD, 18);
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);

        Label header = new Label("Generate Student Transcript", Label.CENTER);
        header.setFont(headerFont);
        add(header);

        lblStatus = new Label("Click 'Generate' to create your transcript.", Label.CENTER);
        lblStatus.setForeground(Color.BLUE);
        add(lblStatus);

        Panel buttonPanel = new Panel();
        btnGenerate = new Button("Generate Transcript");
        btnGenerate.setFont(buttonFont);
        btnGenerate.setBackground(new Color(30, 144, 255));
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.addActionListener(e -> generateTranscript());
        buttonPanel.add(btnGenerate);

        btnBack = new Button("Back to Dashboard");
        btnBack.setFont(buttonFont);
        btnBack.addActionListener(e -> {
            new Dashboard();
            dispose();
        });
        buttonPanel.add(btnBack);
        add(buttonPanel);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void generateTranscript() {
        User currentUser = UserSession.getCurrentUser();
        if (currentUser == null || !"student".equalsIgnoreCase(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "Error: Only students can generate transcripts.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            new loginFrame();
            dispose();
            return;
        }

        lblStatus.setText("Generating transcript, please wait...");
        resultDAO dao = new resultDAO();
        List<ResultRecord> results = dao.getStudentResults(currentUser.getId());

        if (results.isEmpty()) {
            lblStatus.setText("No results found to generate transcript.");
            JOptionPane.showMessageDialog(this, "No results found for your ID. Transcript not generated.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            // This part might need refinement based on how the ReportGenerator is intended to work with multiple records.
            // For a simple implementation, I'll generate a single PDF listing all results.
            
            // Re-adding this declaration and using proper newlines
            StringBuilder transcriptContent = new StringBuilder(); 
            transcriptContent.append("Student Name: ").append(currentUser.getFullName()).append("\n");
            transcriptContent.append("Admission No: ").append(currentUser.getId()).append("\n\n");
            transcriptContent.append("--- Exam Results ---\n");

            for (ResultRecord result : results) {
                transcriptContent.append("Unit: ").append(result.getUnitName())
                                 .append(" | Marks: ").append(result.getMarksObtained())
                                 .append("\n");
            }

            ReportGenerator.generateTranscriptPDF(currentUser.getFullName(), String.valueOf(currentUser.getId()), results);


            lblStatus.setText("Transcript generated successfully! Check project root.");
            JOptionPane.showMessageDialog(this, "Transcript PDF generated successfully in the project root directory.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            lblStatus.setText("Error generating transcript: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error generating transcript: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}