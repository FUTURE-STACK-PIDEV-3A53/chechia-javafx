package org.example.model;

import java.time.LocalDateTime;

public class UserScore {
    private Long id;
    private Long userId;
    private Long gameId;
    private int score;
    private LocalDateTime lastPlayed;
    private String username; // For display purposes

    public UserScore() {
        this.lastPlayed = LocalDateTime.now();
    }

    public UserScore(Long userId, Long gameId, int score) {
        this.userId = userId;
        this.gameId = gameId;
        this.score = score;
        this.lastPlayed = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDateTime getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(LocalDateTime lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
} 