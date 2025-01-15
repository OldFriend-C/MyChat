package com.example.chatui.friendRequest;

import lombok.Getter;

public enum RequestStatus {
    REQUESTED("REQUESTED"),
    ACCEPTED("ACCEPTED"),
    DECLINED("DECLINED"),
    REMOVED("REMOVED");


    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
