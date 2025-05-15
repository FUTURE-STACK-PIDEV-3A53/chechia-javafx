
# Chechia – Java Application
Chechia is a 100% Tunisian application, designed by and for Tunisians, with an interface, content, and a vibe that is deeply rooted in the Tunisian mindset 🇹🇳.
a complete platform that helps manage users, events, gaming, a Reddit-style forum, a Tunisian version of LinkedIn, a programming space, and  exchange program.
The Java application connects to the Symfony backend and provides role-based access for  Admins, and Users.
---
## What This App Does
This application enables users to:
Log in with a role (Admin, User)
Projets d’échange éducatif ou professionnel
Engage in discussions on a Reddit-style forum
Discover and take part in Tunisian events
Build a profile on a Tunisian-style LinkedIn for networking and career opportunities

> Chaque interaction sur chechia vous fait évoluer !
- 🏆 Montez en niveau
- 📈 Suivez votre progression
- 🎯 Relevez des défis


##📄 tables utilise
| Champ            | Type                   | Description                                                     |
|------------------|------------------------|-----------------------------------------------------------------|
| `userID`         | INT (Primary Key)      | Identifiant unique de l'utilisateur                            |
| `username`       | VARCHAR                | Nom d'utilisateur affiché                                      |
| `email`          | VARCHAR                | Adresse email de l'utilisateur                                 |
| `password_hash`  | TEXT / VARCHAR         | Mot de passe chiffré (hashé)                                   |
| `nationality`    | VARCHAR                | Nationalité de l'utilisateur                                   |
| `role`           | ENUM ('user','admin')  | Rôle attribué à l'utilisateur                                  |
| `profile_picture`| TEXT / VARCHAR         | Chemin ou URL vers la photo de profil                          |
| `banner_picture` | TEXT / VARCHAR         | Chemin ou URL vers la bannière de profil                       |
| `bio`            | TEXT                   | Courte biographie ou description personnelle                   |
| `created_at`     | DATETIME               | Date de création du compte                                     |
| `last_login`     | DATETIME               | Dernière connexion de l'utilisateur                            |
| `verified`       | BOOLEAN / TINYINT(1)   | Statut de vérification (1 = vérifié, 0 = non vérifié)          |


 Une application web déployée avec Symfony et une application desktop déployée avec JavaFX, 
 les deux applications se connectant à une base de données MySQL commune garantissent la synchronisation des données partagées.

 
 | Champ         | Type               | Description                                                   |
|---------------|--------------------|---------------------------------------------------------------|
| `id`          | INT (Primary Key)  | Identifiant unique de l'opportunité ou de la candidature      |
| `titre`       | VARCHAR            | Titre du poste ou de l'opportunité                            |
| `lieu`        | VARCHAR            | Lieu géographique (ville, région, remote...)                  |
| `description` | TEXT               | Détails et objectifs de l'opportunité                         |
| `type`        | ENUM('en ligne','présentiel') | Type d'engagement (en ligne ou présentiel)      |
| `experience`  | TEXT / VARCHAR     | Description de l'expérience demandée ou acquise              |
| `annee`       | INT                | Nombre d'années d'expérience requises ou indiquées           |

---

Ces informations permettent à l'utilisateur de filtrer et postuler à des opportunités en fonction de son profil.
## 🛠️ Fonctions implémentées (JavaFX)

### 👤 Côté Utilisateur
  - *Connexion sécurisée avec rôle utilisateur*
  → Authentification avec vérification dans la base de données

- *Consultation des **opportunités professionnelles***
  → Liste dynamique avec filtres (localisation, type, années d’expérience)

- *Participation à un **programme d’échange***
  → Affichage des programmes disponibles, détails, et inscription

- *Navigation dans un **forum de discussions** de type Reddit*
  → Lecture et réponse à des sujets communautaires

- *Exploration des **événements tunisiens***
  → Interface pour découvrir les événements locaux et y participer
  ## 🌟 Modules Fonctionnels

  connexion via la bd centralisé

  ### 👥 Gestion des utilisateurs
- Création de comptes, connexion sécurisée (Admin / Utilisateur)
- Vérification des comptes
- Accès restreint selon le rôle

### 💼 Opportunités professionnelles (LinkedIn tunisien)
- Affichage des offres par type, expérience, lieu
- Postuler à une opportunité
- Suivi des candidatures

### 📅 Événements tunisiens
- Liste des événements à venir
- Détails + inscription

### 🌐 Programme d’échange
- Consultation des programmes disponibles
- Participation / Postulation à un échange

### 🗨️ Forum communautaire (type Reddit)
- Affichage des sujets, discussions par catégorie
- Commentaires et interactions utilisateurs

### 🧑‍💻 Espace programmation
- Partage d’astuces, mini-projets et snippets de code
- Discussions autour du développement (optionnel)

🌐 Fonctions implémentées (Web – Symfony)

👤 Espace Utilisateur

Inscription et connexion sécurisées avec gestion des rôles (admin, user)

Modification du profil : photo, bannière, bio, informations personnelles

Accès personnalisé selon le rôle (vue utilisateur ou vue administrateur)

💼 Opportunités professionnelles

Création et gestion des offres par les admins

Recherche et filtrage par lieu, type, expérience

Candidature en ligne avec confirmation automatique

📅 Événements tunisiens

Affichage dynamique des événements ajoutés par l’admin

Inscription rapide avec feedback utilisateur

Possibilité de liker / commenter un événement (optionnel)

🗨️ Forum communautaire (style Reddit)

Création de sujets par les utilisateurs

Réponses, votes et discussions

Catégorisation des discussions (programmation, événements, entraide...)

🧑‍💻 Espace Programmation

Partage de snippets de code

Discussions techniques ciblées

Intégration possible d’un éditeur Markdown/code dans la version web

🎮 Gamification

Suivi de niveau de l’utilisateur

Défis à accomplir pour gagner des badges

Tableau de bord avec progression visible

📊 Tableau de bord (Admin)

Vue d’ensemble des utilisateurs inscrits

Statistiques d’activité (connexions, participations, candidatures)

Interface de gestion des contenus : forums, événements, opportunités


⚙️ Architecture d’intégration

🗄 Shared MySQL Database: Seamlessly used by Symfony & JavaFX.

🛠 Flask API Calls: Intelligent analysis powered by Symfony.

🔄 Module Synchronization: Smooth integration between Web and Java.

🔗 Coherent Communication: Ensures fluid interactions.

🚀 Installation rapide

🌐 Partie Web
# Clone the repository
https://github.com/FUTURE-STACK-PIDEV-3A53/chachia.git

# Navigate to the project directory
cd chechia

# Install dependencies
composer install

# Create and migrate the database
php bin/console doctrine:database:create
php bin/console doctrine:migrations:migrate

# Start the Symfony server
symfony server:start
  
##📱 Accessible partout

Connectez-vous depuis **votre ordinateur, tablette ou smartphone**.
Chechia offre une expérience fluide et responsive sur tous vos appareils, pour un accès facile à vos événements, forums et profils où que vous soyez.
---
## 🔒 Sécurité et confidentialité

Vous gardez le **contrôle total** sur vos données personnelles.  
Modifiables à tout moment, elles sont **sécurisées** dans un espace privé.
---
## 🤝 Rejoignez-nous

Que vous soyez **user**, ou **administrateur**, CHECHIA vous offre un espace personnalisé pour apprendre, transmettre et progresser.

🔗 [Créer un compte](#) | [Se connecter](#) | [Explorer les opportunites](#)

---
## 🎨 Identité visuelle

**Couleur principale :** `#ff0000`  
Design clair, épuré et accessible pour tous.

---
## Tech Stack

- Java 17
- JavaFX
- HTTP Client (to call Symfony APIs)
- Gson or Jackson (JSON parsing)
- Maven
---




