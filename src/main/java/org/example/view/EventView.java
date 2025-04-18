package org.example.view;

import org.example.controller.EventController;
import org.example.model.Event;

import java.util.Scanner;

public class EventView {
    private final EventController controller = new EventController();
    private final Scanner scanner = new Scanner(System.in);

    public void showMenu() {
        while (true) {
            System.out.println("\n==== Gestion des Événements ====");
            System.out.println("1. Ajouter un événement");
            System.out.println("2. Afficher les événements");
            System.out.println("3. Modifier un événement");
            System.out.println("4. Supprimer un événement");
            System.out.println("5. Quitter");
            System.out.print("Choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1 -> ajouter();
                case 2 -> afficher();
                case 3 -> modifier();
                case 4 -> supprimer();
                case 5 -> System.exit(0);
                default -> System.out.println("❌ Choix invalide.");
            }
        }
    }

    private void ajouter() {
        System.out.print("Nom : ");
        String nom = scanner.nextLine();
        System.out.print("Lieu : ");
        String lieu = scanner.nextLine();
        System.out.print("Date (YYYY-MM-DD) : ");
        String date = scanner.nextLine();
        System.out.print("Type : ");
        String type = scanner.nextLine();
        System.out.print("Montant : ");
        double montant = scanner.nextDouble();
        System.out.print("User ID : ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        Event e = new Event(0, nom, lieu, date, type, montant, userId);
        controller.createEvent(e);
        System.out.println("✅ Événement ajouté.");
    }

    private void afficher() {
        System.out.println("\n📅 Liste des événements :\n");
        for (Event e : controller.getAllEvents()) {
            System.out.println("🆔 ID           : " + e.getId());
            System.out.println("📛 Nom          : " + e.getNomEvent());
            System.out.println("📍 Lieu         : " + e.getLocalisation());
            System.out.println("📅 Date         : " + e.getDateEvent());
            System.out.println("🎭 Type         : " + e.getType());
            System.out.println("💵 Montant      : " + e.getMontant() + " DT");
            System.out.println("👤 ID Utilisateur : " + e.getUserId());
            System.out.println("------------------------------------");
        }
    }


    private void modifier() {
        System.out.print("ID de l'événement à modifier : ");
        int id = scanner.nextInt(); scanner.nextLine();

        System.out.print("Nouveau nom : ");
        String nom = scanner.nextLine();
        System.out.print("Nouveau lieu : ");
        String lieu = scanner.nextLine();
        System.out.print("Nouvelle date : ");
        String date = scanner.nextLine();
        System.out.print("Nouveau type : ");
        String type = scanner.nextLine();
        System.out.print("Nouveau montant : ");
        double montant = scanner.nextDouble();
        System.out.print("User ID : ");
        int userId = scanner.nextInt(); scanner.nextLine();

        Event e = new Event(id, nom, lieu, date, type, montant, userId);
        controller.updateEvent(e);
        System.out.println("✏️ Événement modifié.");
    }

    private void supprimer() {
        System.out.print("ID à supprimer : ");
        int id = scanner.nextInt();
        scanner.nextLine();
        controller.deleteEvent(id);
        System.out.println("🗑️ Événement supprimé.");
    }
}
