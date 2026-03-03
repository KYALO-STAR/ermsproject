package ui;

import dao.resultDAO;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.JFileChooser; // Using Swing's chooser for a better folder-pick experience

public class MarkUploadFrame extends Frame {
    TextField txtExamId, txtFolderPath;
    Button btnSelectFolder, btnProcess, btnBack;
    TextArea logArea;

    public MarkUploadFrame() {
        setTitle("ERMS - Digitization Portal");
        setSize(500, 500);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(230, 230, 230));

        // Top Panel: Input Info
        Panel topPanel = new Panel(new GridLayout(3, 2, 5, 5));
        topPanel.add(new Label("Exam ID (Unit Code):"));
        txtExamId = new TextField();
        topPanel.add(txtExamId);

        topPanel.add(new Label("Scans Folder:"));
        txtFolderPath = new TextField();
        txtFolderPath.setEditable(false);
        topPanel.add(txtFolderPath);

        btnSelectFolder = new Button("Browse Folder");
        topPanel.add(btnSelectFolder);
        
        btnProcess = new Button("Start Digitization");
        btnProcess.setBackground(Color.GREEN);
        topPanel.add(btnProcess);

        add(topPanel, BorderLayout.NORTH);

        // Center: Progress Log
        logArea = new TextArea("Ready to process...\nNote: Ensure files are named 'StudentID_Marks.pdf' (e.g. 7001_85.pdf)\n");
        add(logArea, BorderLayout.CENTER);

        // Bottom: Navigation
        btnBack = new Button("Return to Dashboard");
        add(btnBack, BorderLayout.SOUTH);

        // --- Event Handling ---

        btnSelectFolder.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                txtFolderPath.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        btnProcess.addActionListener(e -> startProcessing());

        btnBack.addActionListener(e -> {
            new Dashboard();
            dispose();
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startProcessing() {
        String folderPath = txtFolderPath.getText();
        String examIdStr = txtExamId.getText();

        if (folderPath.isEmpty() || examIdStr.isEmpty()) {
            logArea.append("ERROR: Fill all fields!\n");
            return;
        }

        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (files == null || files.length == 0) {
            logArea.append("No PDF files found in this directory.\n");
            return;
        }

        int examId = Integer.parseInt(examIdStr);
        resultDAO dao = new resultDAO();
        int successCount = 0;

        for (File file : files) {
            try {
                // Parsing filename: "StudentID_Marks.pdf" -> ["StudentID", "Marks"]
                String nameOnly = file.getName().substring(0, file.getName().lastIndexOf('.'));
                String[] parts = nameOnly.split("_");
                
                int studentId = Integer.parseInt(parts[0]);
                int marks = Integer.parseInt(parts[1]);
                String path = file.getAbsolutePath();

                if (dao.saveResult(examId, studentId, marks, path)) {
                    logArea.append("Digitized: " + studentId + " [Mark: " + marks + "]\n");
                    successCount++;
                }
            } catch (Exception ex) {
                logArea.append("Skipped " + file.getName() + ": Invalid naming format.\n");
            }
        }
        logArea.append("--- FINISHED: " + successCount + " booklets linked ---\n");
    }
}