package com.example.chatui.aboutMessage;

import com.example.chatui.aboutUser.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Message {
    private User senderUser;
    private User receiverUser;
    private String messageType;
    private String messageContent;
    private Date createdAt;
}
