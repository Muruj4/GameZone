package com.example.gamezone;

public class Player {
    private String playerId;
    private String fullName;
    private String email;
    private String skillLevel;
    private String preferredGame;
    private String imageUrl;

    // Default constructor required for DataSnapshot.getValue(Player.class)
    public Player() {
    }

    // Constructor for creating a player with all details
    public Player(String playerId, String fullName, String email, String skillLevel, String preferredGame, String imageUrl) {
        this.playerId = playerId;
        this.fullName = fullName;
        this.email = email;
        this.skillLevel = skillLevel;
        this.preferredGame = preferredGame;
        this.imageUrl = imageUrl;
    }

    // Constructor for creating a player with just name and email
    public Player(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    // Getters and Setters
    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public String getPreferredGame() {
        return preferredGame;
    }

    public void setPreferredGame(String preferredGame) {
        this.preferredGame = preferredGame;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
