package com.mila.utils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.Chunk;
import com.mila.model.ProgrammeEchange;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PDFGenerator {
    
    public static void generateProgramsPDF(List<ProgrammeEchange> programmes, String outputPath) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));
        
        document.open();
        
        // En-tête du document
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.RED);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        
        Paragraph title = new Paragraph("Liste des Programmes d'Échange", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Ajouter chaque programme
        for (ProgrammeEchange programme : programmes) {
            // Titre du programme
            Paragraph progTitle = new Paragraph(programme.getNomProgramme(), headerFont);
            progTitle.setSpacingBefore(15);
            document.add(progTitle);
            
            // Détails du programme
            document.add(new Paragraph("Type: " + programme.getType().getLibelle(), normalFont));
            document.add(new Paragraph("Nationalité: " + programme.getNationalite().toString(), normalFont));
            document.add(new Paragraph("Durée: " + programme.getDuree() + " mois", normalFont));
            
            // Ligne de séparation
            LineSeparator line = new LineSeparator();
            line.setLineColor(BaseColor.LIGHT_GRAY);
            document.add(new Chunk(line));
        }
        
        document.close();
    }
}