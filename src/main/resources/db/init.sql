-- Création de la base de données
CREATE DATABASE IF NOT EXISTS chechia;
USE chechia;

-- Table des jeux
CREATE TABLE IF NOT EXISTS game (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    picture VARCHAR(255),
    description TEXT,
    number_of_players INT,
    file_path VARCHAR(255),
    genre VARCHAR(50),
    min_players INT,
    max_players INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des salles de jeu
CREATE TABLE IF NOT EXISTS gameroom (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    capacity INT NOT NULL,
    location VARCHAR(255),
    game_id BIGINT,
    date_time TIMESTAMP,
    bot_enabled BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (game_id) REFERENCES game(id)
);

-- Table des joueurs
CREATE TABLE IF NOT EXISTS player (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table de liaison entre joueurs et salles
CREATE TABLE IF NOT EXISTS player_gameroom (
    player_id BIGINT,
    gameroom_id BIGINT,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (player_id, gameroom_id),
    FOREIGN KEY (player_id) REFERENCES player(id),
    FOREIGN KEY (gameroom_id) REFERENCES gameroom(id)
);