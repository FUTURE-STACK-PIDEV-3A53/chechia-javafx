package org.example.model;

import java.time.LocalDateTime;

public class Game {
    private Long id;
    private String name;
    private String picture;
    private String description;
    private int number_of_players;
    private String file_path;
    private LocalDateTime created_at;
    private GameRoom gameRoom;
    private String genre;
    private int minPlayers;
    private int maxPlayers;

    public Game() {
        this.created_at = LocalDateTime.now();
    }

    public Game(String name, String picture, String description, int number_of_players, String file_path) {
        this.name = name;
        this.picture = picture;
        this.description = description;
        this.number_of_players = number_of_players;
        this.file_path = file_path;
        this.created_at = LocalDateTime.now();
        this.minPlayers = 1;
        this.maxPlayers = number_of_players;
    }

    public Game(String name, String description, int number_of_players) {
        this.name = name;
        this.description = description;
        this.number_of_players = number_of_players;
        this.created_at = LocalDateTime.now();
        this.minPlayers = 1;
        this.maxPlayers = number_of_players;
    }

    public Game(String name, String description, int minPlayers, int maxPlayers) {
        this.name = name;
        this.description = description;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.number_of_players = maxPlayers;
        this.created_at = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumber_of_players() {
        return number_of_players;
    }

    public void setNumber_of_players(int number_of_players) {
        this.number_of_players = number_of_players;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public GameRoom getGameRoom() {
        return gameRoom;
    }

    public void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}