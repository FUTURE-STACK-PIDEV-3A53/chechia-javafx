package org.example.model;

import java.time.LocalDateTime;

public class Player {
    private Long id;
    private Long gameRoomId;
    private int score;
    private String username;
    private String gameRoomName;
    private LocalDateTime created;
    private GameRoom gameRoom;

    public Player() {}

    public Player(GameRoom gameRoom, int score) {
        this.gameRoom = gameRoom;
        this.gameRoomId = gameRoom.getId();
        this.score = score;
        this.gameRoomName = gameRoom.getName();
        this.created = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGameRoomId() {
        return gameRoomId;
    }

    public void setGameRoomId(Long gameRoomId) {
        this.gameRoomId = gameRoomId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGameRoomName() {
        return gameRoomName;
    }

    public void setGameRoomName(String gameRoomName) {
        this.gameRoomName = gameRoomName;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public GameRoom getGameRoom() {
        return gameRoom;
    }

    public void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
        this.gameRoomId = gameRoom.getId();
    }
}