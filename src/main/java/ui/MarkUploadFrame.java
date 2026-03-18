package ui;

import java.awt.Button;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import dao.resultDAO;
import database.UserSession;
import ui.upload.UploaderServer;

/**
 * MarkUploadFrame: keeps the UI simple but now can also start an embedded HTTP upload server
 * so phones can POST PDFs directly to the desktop application.
 */
public class MarkUploadFrame extends Frame {
    TextField txtExamId;
    Label lblStatus;
    Label lblUrl;
    private UploaderServer server;
    private Button btnPhoneUpload;

    public MarkUploadFrame() {
        setTitle("ERMS Scanner Portal - Digitizing Booklets");

        // Enforce role-based access: only 'lecturer' and 'admin' may open this screen
        if (UserSession.getCurrentUser() == null
                || "student".equalsIgnoreCase(UserSession.getCurrentUser().getRole())) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Access denied: only lecturers and admins may use the scanner.");
            new loginFrame();
            dispose();
            return;
        }
        setSize(500, 320);
        setLayout(new GridLayout(7, 1, 10, 10));

        add(new Label("Enter Exam ID (Unit Code):", Label.CENTER));
        txtExamId = new TextField();
        add(txtExamId);

        Button btnScanFolder = new Button("Select Folder & Start Scanning");
        btnScanFolder.setBackground(Color.ORANGE);
        add(btnScanFolder);

        btnPhoneUpload = new Button("Start Phone Upload Server");
        add(btnPhoneUpload);

        lblUrl = new Label("Upload URL: (server not running)", Label.CENTER);
        add(lblUrl);

        lblStatus = new Label("Ready...", Label.CENTER);
        add(lblStatus);

        Button btnClose = new Button("Close");
        add(btnClose);

        // --- SCANNER LOGIC ---
        btnScanFolder.addActionListener(e -> {
            String examIdStr = txtExamId.getText();
            if (examIdStr.isEmpty()) {
                lblStatus.setText("Error: Enter Exam ID first!");
                return;
            }

            try {
                int examId = Integer.parseInt(examIdStr);
                startFolderScanner(examId);
            } catch (NumberFormatException ex) {
                lblStatus.setText("Error: Exam ID must be a number!");
            }
          
 });

        btnPhoneUpload.addActionListener(e -> {
            if (server == null) {
                String examIdStr = txtExamId.getText();
                if (examIdStr.isEmpty()) {
                    lblStatus.setText("Error: Enter Exam ID first!");
                    return;
                }
                try {
                    int examId = Integer.parseInt(examIdStr);
                    int port = 8080;
                    server = new UploaderServer(port, examId);
                    server.start();
                    String ip = InetAddress.getLocalHost().getHostAddress();
                    lblUrl.setText("Upload URL: http://" + ip + ":" + port + "/upload");
                    btnPhoneUpload.setLabel("Stop Phone Upload Server");
                    lblStatus.setText("Server running — open the URL on your phone's browser.");
                } catch (NumberFormatException ex) {
                    lblStatus.setText("Error: Exam ID must be a number!");
                } catch (IOException ex) {
                    lblStatus.setText("Failed to start server: " + ex.getMessage());
                    server = null;
                }
            } else {
                server.stop();
                server = null;
                lblUrl.setText("Upload URL: (server not running)");
                btnPhoneUpload.setLabel("Start Phone Upload Server");
                lblStatus.setText("Server stopped");
            }
        });

        btnClose.addActionListener(e -> {
            if (server != null) server.stop();
            dispose();
        });

        // Handle window close button
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { if (server != null) server.stop(); dispose(); }
        });

        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void startFolderScanner(int examId) {
        // 1. Open Folder Picker
        FileDialog fd = new FileDialog(this, "Select Folder Containing Marked Booklets", FileDialog.LOAD);
        fd.setVisible(true);

        if (fd.getDirectory() != null) {
            File folder = new File(fd.getDirectory());
            // Filter only PDF files
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

            if (files == null || files.length == 0) {
                lblStatus.setText("No PDF booklets found in this folder.");
                return;
            }

            resultDAO dao = new resultDAO();
            int successCount = 0;

            for (File file : files) {
                // 2. Validate PDF using PDFBox (Digital Integrity Scan)
                // This ensures the file is a real PDF and not corrupt
                try (PDDocument doc = Loader.loadPDF(file)) {

                    // 3. Extract Data from Filename: "StudentID_Marks.pdf" (e.g., 71_85.pdf)
                    String fileName = file.getName().replace(".pdf", "");
                    String[] parts = fileName.split("_");

                    if (parts.length < 2) {
                        System.out.println("Skipping: " + file.getName() + " (Wrong format)");
                        continue;
                    }

                    int studentId = Integer.parseInt(parts[0].trim());
                    int marks = Integer.parseInt(parts[1].trim());
                    String path = file.getAbsolutePath();

                    // 4. Save to Aiven MySQL
                    if (dao.saveResult(examId, studentId, marks, path)) {
                        successCount++;
                        System.out.println("Digitized Student ID: " + studentId);
                    }

                } catch (Exception e) {
                    System.err.println("Error scanning/validating booklet: " + file.getName());
                }
            }

            lblStatus.setText("Scan Complete! " + successCount + " booklets digitized.");
            javax.swing.JOptionPane.showMessageDialog(this, "Success! Digitized " + successCount + " records to Aiven.");
        }
    }
}