package com.example.chatui.MQChat;

import com.alibaba.fastjson.JSON;
import com.example.chatui.aboutFriend.RequestFriend;
import com.example.chatui.aboutUser.User;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import javafx.application.Platform;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import static com.example.chatui.ChatApp.requestUsers;
import static com.example.chatui.ChatApp.updateSilderBar;
import static com.example.chatui.basic.LoginBasicTool.avatarBae64ToImage;

public class RequestClient {
    private static final String HOST = "192.168.1.108"; // RabbitMQ 服务的主机
    private static final int PORT = 5672; // RabbitMQ 默认端口
    private static final String USERNAME = "remote";
    private static final String PASSWORD = "123456"; // RabbitMQ 密码
    private Connection connection;
    private static final String virtualHost="/";
    private Channel channel;
    private final String friendRequestQueueName;

    public RequestClient(String username) {
        friendRequestQueueName=username+"/friendRequest";
        try {
            // 创建与 RabbitMQ 的连接
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setPort(PORT);
            factory.setUsername(USERNAME);
            factory.setPassword(PASSWORD);
            factory.setVirtualHost(virtualHost); // 虚拟主机
            connection = factory.newConnection();
            channel = connection.createChannel();

            // 为用户创建和订阅一个以用户名命名的监听好友请求的队列
            channel.queueDeclare(friendRequestQueueName, true, false, false, null);
            System.out.println(" [*] Waiting for messasges in queue: " + friendRequestQueueName);

            // TODO:接收好友请求后通过侧边栏展现好友请求
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Platform.runLater(()->{
                    byte[] body = delivery.getBody();
                    String message = new String(body, StandardCharsets.UTF_8);
                    RequestFriend fromfriend=JSON.parseObject(message, RequestFriend.class);
                    Date requestTime=fromfriend.getRequestTime();  //获取用户请求时间
                    User fromUser=  new User();
                    fromUser.setUsername(fromfriend.getUsername());  //得到请求用户的名称
                    String avatarString=fromfriend.getAvatar();
                    fromUser.setAvatar(avatarBae64ToImage(avatarString));  //得到请求用户的头像
                    requestUsers.add(fromUser);
                    updateSilderBar();
                });
            };
            channel.basicConsume(friendRequestQueueName, true, deliverCallback, consumerTag -> { });

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

}