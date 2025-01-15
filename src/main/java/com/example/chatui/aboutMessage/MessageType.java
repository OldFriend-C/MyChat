package com.example.chatui.aboutMessage;

import lombok.Getter;

@Getter
public enum MessageType {
    FRIENDREQUEST("FRIENDREQUEST"),
    MESSAGE("MESSAGE");
    private final String description;

    MessageType(String description) {
            this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
