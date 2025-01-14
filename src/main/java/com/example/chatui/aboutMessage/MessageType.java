package com.example.chatui.aboutMessage;

import lombok.Getter;

@Getter
public enum MessageType {
    FRIENDREQUEST("friendRequest"),
    MESSAGE("message");
    private final String description;

    MessageType(String description) {
            this.description = description;
    }

}
