package com.example.chatui.aboutMessage;

import lombok.Getter;

@Getter
public enum InfoType {
    FRIENDREQUEST("FRIENDREQUEST"),
    MESSAGE("MESSAGE");
    private final String description;

    InfoType(String description) {
            this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
