package com.example.chatui.aboutFriend;

import com.example.chatui.friendRequest.RequestStatus;
import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchFriend {
    private String username;
    private Image avatar;
    private String requestStatus;
}
