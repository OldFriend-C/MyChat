package com.example.chatui.aboutMessage;

import javafx.scene.image.Image;

public class Message {
    private String message;
    private String sendername;
    private Image avatar;
    public Message(String name, String message, Image avatar) {
        this.message = message;
        this.sendername = name;
        this.avatar = avatar;
    }
    public String getSenderName() {
        return sendername;
    }
    public Image getAvatar() {
        return avatar;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setSendername(String sendername) {
        this.sendername = sendername;
    }
    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }
}
