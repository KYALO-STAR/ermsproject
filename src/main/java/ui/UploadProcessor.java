package ui;

import java.io.File;

import dao.resultDAO;

public class UploadProcessor {

    /**
     * Process a PDF file whose filename follows the pattern: StudentID_Marks.pdf
     * Returns true if saved successfully via resultDAO.
     */
    public static boolean processPdf(File file, int examId) {
        if (file == null || !file.exists()) return false;

        String fileName = file.getName().replaceAll("(?i)\\.pdf$", "");
        String[] parts = fileName.split("_");
        if (parts.length < 2) {
            System.err.println("UploadProcessor: filename not in expected format: " + file.getName());
            return false;
        }

        try {
            int studentId = Integer.parseInt(parts[0].trim());
            int marks = Integer.parseInt(parts[1].trim());
            String path = file.getAbsolutePath();

            resultDAO dao = new resultDAO();
            return dao.saveResult(examId, studentId, marks, path);
        } catch (Exception e) {
            System.err.println("UploadProcessor: error parsing filename: " + e.getMessage());
            return false;
        }
    }
}
