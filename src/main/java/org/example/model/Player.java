package org.example.model;

import java.time.LocalDateTime;

public class Player {
    private Long id;
    private Long gameroomId;
    private int score;
    private LocalDateTime created;
    private GameRoom gameRoom;

    public Player() {}

    public Player(GameRoom gameRoom, int score) {
        this.gameRoom = gameRoom;
        this.gameroomId = gameRoom.getId();
        this.score = score;
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
        return gameroomId;
    }

    public void setGameRoomId(Long gameroomId) {
        this.gameroomId = gameroomId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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
        this.gameroomId = gameRoom.getId();
    }
}