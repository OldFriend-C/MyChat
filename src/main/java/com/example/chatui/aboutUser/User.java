package com.example.chatui.aboutUser;

import com.alibaba.fastjson.annotation.JSONType;
import com.example.chatui.basic.UserSerializer;
import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JSONType(serializer = UserSerializer.class)
public class User {
    private String username;
    private Image avatar;

}