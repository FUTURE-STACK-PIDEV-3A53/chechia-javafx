package org.example.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.model.GameRoom;
import org.example.model.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFGenerator {
    public static String generateRankingPDF(GameRoom gameRoom, List<Player> players) throws IOException, DocumentException {
        // Créer le nom du fichier avec la date et l'heure
        String fileName = "downloads/ranking_" + gameRoom.getName() + "_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

        // Créer le dossier downloads s'il n'existe pas
        new File("downloads").mkdirs();

        // Créer le document PDF
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        // Ajouter le titre
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Paragraph title = new Paragraph("Classement - " + gameRoom.getName(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // Ajouter les informations de la salle
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph roomInfo = new Paragraph(
            "Salle : " + gameRoom.getName() + "\n" +
            "Description : " + gameRoom.getDescription() + "\n" +
            "Capacité : " + gameRoom.getCapacity() + " joueurs\n" +
            "Date : " + gameRoom.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            normalFont
        );
        document.add(roomInfo);
        document.add(new Paragraph("\n"));

        // Créer le tableau des classements
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 5, 3});

        // Style pour les en-têtes
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        PdfPCell cell;

        // Ajouter les en-têtes
        cell = new PdfPCell(new Phrase("Rang", headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Joueur", headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Score", headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // Ajouter les données des joueurs
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            
            cell = new PdfPCell(new Phrase(String.valueOf(i + 1), normalFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(player.getUsername(), normalFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(player.getScore()), normalFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
        }

        document.add(table);
        document.add(new Paragraph("\n"));

        // Ajouter un pied de page
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph footer = new Paragraph(
            "Document généré automatiquement le " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            footerFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        // Fermer le document
        document.close();

        return fileName;
    }
} 