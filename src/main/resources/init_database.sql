-- Création de la base de données si elle n'existe pas
CREATE DATABASE IF NOT EXISTS chechia;
USE chechia;

-- Table des jeux
CREATE TABLE IF NOT EXISTS game (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    picture VARCHAR(255),
    description TEXT,
    number_of_players INT,
    file_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des salles de jeu
CREATE TABLE IF NOT EXISTS gameroom (
    id INT PRIMARY KEY AUTO_INCREMENT,
    game_id INT,
    location VARCHAR(255),
    date_time DATETIME,
    bot_enabled BOOLEAN DEFAULT false,
    FOREIGN KEY (game_id) REFERENCES game(id)
);

-- Table des joueurs
CREATE TABLE IF NOT EXISTS player (
    id INT PRIMARY KEY AUTO_INCREMENT,
    gameroom_id INT,
    score INT DEFAULT 0,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (gameroom_id) REFERENCES gameroom(id)
);