package com.example.chatui.aboutUser;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import java.lang.reflect.Type;

import static com.example.chatui.basic.LoginBasicTool.avatarBae64ToImage;

public class UserDeserializer implements ObjectDeserializer {
    @Override
    public Object deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        JSONObject jsonObject = parser.parseObject();

        String username = jsonObject.getString("username");
        String avatarBase64 = jsonObject.getString("avatar");

        User user = new User();
        user.setUsername(username);
        user.setAvatar(avatarBae64ToImage(avatarBase64));
        return user;
    }



    @Override
    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }
}
