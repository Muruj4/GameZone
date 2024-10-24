package com.example.gamezone;

public class Player {
    public String playerId;
    public String skillLevel;
    public String preferredGame;
    public String imageUrl;
    public String fullName; // عدلت الاسم إلى أحرف صغيرة
    public String email;

    // Constructor الأول لإنشاء لاعب جديد باستخدام الاسم والبريد الإلكتروني
    public Player(String fullName, String email) {
        this.fullName = fullName; // استخدم الحروف الصغيرة هنا أيضًا
        this.email = email;
    }

    // Constructor الثاني يتعامل مع معلومات اللاعب الأخرى
    public Player(String playerId, String skillLevel, String preferredGame, String imageUrl) {
        this.playerId = playerId;
        this.skillLevel = skillLevel;
        this.preferredGame = preferredGame;
        this.imageUrl = imageUrl;
    }

    // Getter and Setter methods
    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullName) { // الدالة تم تعديلها لتطابق اسم المتغير
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() { // الدالة تم تعديلها لتطابق اسم المتغير
        return fullName;
    }

    // Constructor افتراضي مطلوب لاستدعاء DataSnapshot.getValue(Player.class)
    public Player() {
    }
}
