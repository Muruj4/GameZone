package com.example.gamezone;
import java.io.Serializable;
import java.io.Serializable;
import java.util.List;

public class Video implements Serializable {
    private String title;
    private String description;
    private String author;
    private String videoUrl;
    private String imageUrl;

    private double prizeAmount;    // Add prizeAmount field
    private String level;          // Add level field
    private List<String> modes;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    // Getters and Setters for basic details
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // New getter and setter for mode
    public List<String> getModes() {
        return modes;
    }

    public void setModes(List<String> modes) {
        this.modes = modes;
    }


    // New getter and setter for prizeAmount
    public double getPrizeAmount() {
        return prizeAmount;
    }

    public void setPrizeAmount(double prizeAmount) {
        this.prizeAmount = prizeAmount;
    }

    // New getter and setter for level
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}



