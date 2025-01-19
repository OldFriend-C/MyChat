package com.example.chatui.aboutMessage;

import com.example.chatui.aboutUser.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SendMsg {
    private String senderUseraname;
    private String recieverUseraname;
    private String messageType;
    private String messageContent;
    private Date createdAt;
}
