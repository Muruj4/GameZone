package com.example.gamezone;
import java.io.Serializable;
import java.util.List;

public class Video implements Serializable {
    private String title;
    private String description;
    private String author;
    private String videoUrl;
    private String imageUrl;
    private double prizeAmount;
    private String mode;
    private String level;
    private String category;  // Added category field

    // Getters and Setters for basic details
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Getters and Setters for prizeAmount, modes, level, and category
    public double getPrizeAmount() { return prizeAmount; }
    public void setPrizeAmount(double prizeAmount) { this.prizeAmount = prizeAmount; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}



