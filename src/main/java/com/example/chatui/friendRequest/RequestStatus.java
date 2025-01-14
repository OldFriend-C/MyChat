package com.example.chatui.friendRequest;

import lombok.Getter;

@Getter
public enum RequestStatus {
    REQUESTED("Requested"),
    ACCEPTED("Accepted"),
    DECLINED("Declined");


    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

}
