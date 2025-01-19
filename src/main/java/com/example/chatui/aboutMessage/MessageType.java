package com.example.chatui.aboutMessage;

import lombok.Getter;

@Getter
public enum MessageType {
    IMAGE("Image"),
    TEXT("Text"),
    FILE("File");
    private final String description;

    MessageType(String description) {
            this.description = description;
        }


}
