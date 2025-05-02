package org.example.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;

public class GameRoom {
    private Long id;
    private String name;
    private String description;
    private int capacity;
    private String location;
    private LocalDateTime dateTime;
    private boolean botEnabled;
    private Long gameId;
    private ObservableList<Game> games = FXCollections.observableArrayList();
    private ObservableList<Player> players = FXCollections.observableArrayList();

    public GameRoom() {
        this.games = FXCollections.observableArrayList();
        this.dateTime = LocalDateTime.now();
    }

    public GameRoom(String name, String description, int capacity) {
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.games = FXCollections.observableArrayList();
        this.dateTime = LocalDateTime.now();
    }

    public GameRoom(String name, String description, int capacity, String location, boolean bot_enabled) {
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.location = location;
        this.botEnabled = bot_enabled;
        this.dateTime = LocalDateTime.now();
        this.games = FXCollections.observableArrayList();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isBotEnabled() {
        return botEnabled;
    }

    public void setBotEnabled(boolean botEnabled) {
        this.botEnabled = botEnabled;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public ObservableList<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
            player.setGameRoom(this);
        }
    }

    public ObservableList<Game> getGames() {
        return games;
    }

    public Game getGame() {
        return games.isEmpty() ? null : games.get(0);
    }

    public void addGame(Game game) {
        if (!games.contains(game)) {
            games.add(game);
            game.setGameRoom(this);
        }
    }

    public void removeGame(Game game) {
        if (games.contains(game)) {
            games.remove(game);
            game.setGameRoom(null);
        }
    }

    public Player getPlayer() {
        return players.isEmpty() ? null : players.get(0);
    }

    @Override
    public String toString() {
        return name + (description != null ? " - " + description : "");
    }

    public void setGame(Game game) {
        if (!games.contains(game)) {
            games.add(game);
            game.setGameRoom(this);
        }
    }
}