package com.example.gamezone;

import java.util.List;

public class Tournament {
    private String id;                // Unique ID for each tournament
    private String title;
    private String description;
    private List<String> mode;
    private String prize;
    private String duration;
    private String startDate;
    private String level;
    private List<String> games;// List of games for tournaments with multiple games

    // Default constructor required for Firebase
    public Tournament() {
    }

    public Tournament(String id, String title, String description, List<String> mode, String prize,
                      String duration, String startDate, String level, List<String> games) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.mode = mode;
        this.prize = prize;
        this.duration = duration;
        this.startDate = startDate;
        this.level= level;
        this.games = games;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getMode() {
        return mode;
    }

    public void setMode(List<String> mode) {
        this.mode = mode;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
    public List<String> getGames() {
        return games;
    }

    public void setGames(List<String> games) {
        this.games = games;
    }
}
