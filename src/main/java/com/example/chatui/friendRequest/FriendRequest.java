package com.example.chatui.friendRequest;

import com.example.chatui.aboutMessage.InfoType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendRequest {
    private InfoType infoType;
    private String fromUserUsername;
    private String toUserUsername;
    private RequestStatus status;
}
