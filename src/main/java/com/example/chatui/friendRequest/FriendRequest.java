package com.example.chatui.friendRequest;

import com.example.chatui.aboutMessage.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendRequest {
    private MessageType messageType;
    private String fromUserUsername;
    private String toUserUsername;
    private RequestStatus status;
}
