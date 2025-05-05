package org.example.utils;

import java.io.FileOutputStream;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFGenerator {
    public static void generateReservationReport(String filePath, List<String> reservations) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            document.add(new Paragraph("Reservation Report"));
            document.add(new Paragraph(" "));

            for (String reservation : reservations) {
                document.add(new Paragraph(reservation));
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
