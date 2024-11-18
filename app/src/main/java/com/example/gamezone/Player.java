package com.example.gamezone;



public class Player {

    private String playerId;

    private String fullName;

    private String email;

    private String skillLevel;

    private String preferredGame;

    private String imageUrl;

    private String nickname;

    private String nationality;

    private String birthdate;





    // Constructor for creating a player with all details

    public Player(String playerId, String fullName, String email, String skillLevel, String preferredGame, String imageUrl, String nickname, String nationality, String birthdate) {

        this.playerId = playerId;

        this.fullName = fullName;

        this.email = email;

        this.skillLevel = skillLevel;

        this.preferredGame = preferredGame;

        this.imageUrl = imageUrl;

        this.nickname = nickname;

        this.nationality = nationality;

        this.birthdate = birthdate;

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

    public String getNickname() {

        return nickname;

    }



    public void setNickname(String nickname) {

        this.nickname = nickname;

    }



    public String getNationality() {

        return nationality;

    }



    public void setNationality(String nationality) {

        this.nationality = nationality;

    }



    public String getBirthdate() {

        return birthdate;

    }



    public void setBirthdate(String birthdate) {

        this.birthdate = birthdate;

    }

}