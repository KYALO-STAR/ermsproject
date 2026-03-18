package utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts; // PDFBox 3.x way
import java.io.IOException;
import java.util.List; // Import List
import models.ResultRecord; // Import ResultRecord

public class ReportGenerator {

    public static void generateStudentPDF(String studentName, String studentId, String unit, int marks) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                // PDFBox 3.0 uses a slightly different font loading system
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("PROVISIONAL RESULT SLIP");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Student Name: " + studentName);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Admission No: " + studentId);
                contentStream.newLineAtOffset(0, -40);
                contentStream.showText("Unit Code: " + unit);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Marks Obtained: " + marks);
                contentStream.endText();
            }

            String filename = studentId + "_Result.pdf";
            document.save(filename);
            System.out.println("PDF Generated: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateTranscriptPDF(String studentName, String studentId, List<ResultRecord> results) {
        PDPageContentStream contentStream = null;
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("ACADEMIC TRANSCRIPT");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText("Student Name: " + studentName);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Admission No: " + studentId);
            contentStream.newLineAtOffset(0, -30);
            contentStream.showText("---------------------------------------------------------------------");
            contentStream.newLineAtOffset(0, -15);
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
            contentStream.showText(String.format("%-40s %s", "Unit", "Marks"));
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("---------------------------------------------------------------------");
            
            float currentY = 600; // Starting Y for results
            for (ResultRecord result : results) {
                contentStream.newLineAtOffset(0, -15);
                currentY -= 15;
                // Check if new page is needed
                if (currentY < 50) {
                    contentStream.endText();
                    contentStream.close(); // Close current stream

                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page); // Create new stream
                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    contentStream.newLineAtOffset(50, 750);
                    currentY = 750;
                }
                contentStream.showText(String.format("%-40s %d", result.getUnitName(), result.getMarksObtained()));
            }
            contentStream.endText();

            String filename = studentId + "_Transcript.pdf";
            document.save(filename);
            System.out.println("PDF Transcript Generated: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (contentStream != null) {
                try {
                    contentStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}