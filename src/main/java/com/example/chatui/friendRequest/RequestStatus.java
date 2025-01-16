package com.example.chatui.friendRequest;

import lombok.Getter;

public enum RequestStatus {
    REQUESTED("Requested"),
    ACCEPTED("Accepted"),
    DECLINED("Declined"),
    REMOVED("Removed"),
    IRRELEVANT("Irrelevant"),
    PENDING("Pending");

    @Getter
    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }



}
