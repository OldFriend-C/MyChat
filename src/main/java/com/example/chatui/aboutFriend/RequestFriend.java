package com.example.chatui.aboutFriend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RequestFriend {
    private String username;
    private String avatar;
    private Date requestTime;

}
