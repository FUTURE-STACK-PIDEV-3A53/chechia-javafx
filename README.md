# Chachia - Gestionnaire de Salles de Jeux

Application JavaFX pour la gestion de salles de jeux et leurs jeux associés.

## Fonctionnalités

- Gestion des salles de jeux (CRUD)
  - Création de nouvelles salles
  - Modification des informations
  - Suppression de salles
  - Affichage de la liste des salles

- Gestion des jeux (CRUD)
  - Ajout de nouveaux jeux
  - Modification des détails
  - Suppression de jeux
  - Association avec les salles

- Validation des données
  - Contrôle des champs obligatoires
  - Vérification des doublons
  - Validation des nombres de joueurs

## Prérequis

- Java JDK 17 ou supérieur
- Maven
- JavaFX SDK
- Scene Builder (pour modification de l'interface)

## Installation

1. Cloner le dépôt :
```bash
git clone [URL_DU_REPO]
cd chachiaPI
```

2. Compiler le projet :
```bash
mvn clean install
```

3. Exécuter l'application :
```bash
mvn javafx:run
```

## Structure du Projet

- `src/main/java/org/example/`
  - `model/` : Classes modèles (GameRoom, Game)
  - `controller/` : Contrôleurs JavaFX
  - `App.java` : Point d'entrée de l'application

- `src/main/resources/`
  - `main.fxml` : Interface utilisateur principale

## Utilisation

1. Lancer l'application
2. Utiliser les onglets pour naviguer entre la gestion des salles et des jeux
3. Utiliser les boutons pour effectuer les opérations CRUD
4. Remplir les formulaires en respectant les règles de validation

## Développement

Le projet suit une architecture MVC :
- Modèle : Classes métier avec validation
- Vue : Fichiers FXML pour l'interface
- Contrôleur : Logique de l'application