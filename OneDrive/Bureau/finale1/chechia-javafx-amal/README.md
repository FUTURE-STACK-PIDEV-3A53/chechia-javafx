# Projet JavaFX - Gestion des Programmes d'Échange et Postulations

Ce projet implémente une application JavaFX pour gérer les programmes d'échange et les postulations des étudiants.

## Structure du Projet

- `model` : Contient les classes de modèle (Postulation, ProgrammeEchange)
- `view` : Contient les fichiers FXML pour l'interface utilisateur
- `controller` : Contient les contrôleurs JavaFX
- `dao` : Contient les classes d'accès aux données
- `util` : Contient les classes utilitaires

## Relation entre les Entités

- Un Programme d'Échange (prg_echange) peut avoir plusieurs Postulations
- Une Postulation appartient à un seul Programme d'Échange

## Fonctionnalités

- Affichage des programmes d'échange disponibles
- Création, modification et suppression des programmes d'échange
- Affichage des postulations pour chaque programme
- Création, modification et suppression des postulations