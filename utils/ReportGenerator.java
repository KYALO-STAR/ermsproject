package utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts; // PDFBox 3.x way
import java.io.IOException;

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
}