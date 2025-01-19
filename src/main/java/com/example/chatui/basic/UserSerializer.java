package com.example.chatui.basic;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.example.chatui.aboutUser.User;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;

public class UserSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        User user = (User) object;
        var jsonObject = new JSONObject();

        jsonObject.put("username", user.getUsername());

        if (user.getAvatar() != null) {
            // 将 Image 对象转换为 Base64 字符串
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(user.getAvatar(), null), "png", baos);
            byte[] imageBytes = baos.toByteArray();
            String avatarBase64 = Base64.getEncoder().encodeToString(imageBytes);
            jsonObject.put("avatar", avatarBase64);
        }

        serializer.write(jsonObject);
    }
}
