package com.example.gamezone;

public class Player {
    public String FullName, email;
    public Player(String FullName, String email){
        this.FullName = FullName;
        this.email = email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullName) {
        this.FullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return FullName;
    }
}