package com.example.chatui.aboutUser;

import javafx.scene.image.Image;
import lombok.Setter;

public class User {
    private String username;
    private Image avatar;

    public User(String username, Image avatar) {
        this.username = username;
        this.avatar = avatar;
    }
    public User(){}

    public String getName() {
        return username;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }
}