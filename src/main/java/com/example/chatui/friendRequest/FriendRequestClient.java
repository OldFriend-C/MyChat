package com.example.chatui.friendRequest;

import com.alibaba.fastjson.JSON;
import com.example.chatui.aboutMessage.MessageType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class FriendRequestClient {
    private static final String QUEUE_NAME = "friendRequest";
    private static final String HOST="192.168.1.108";
    private static final int PORT=5672;
    private static final String USERNAME="remote";
    private static final String PASSWORD="123456";

    private Connection connection;
    private Channel channel;

    public FriendRequestClient() {
        initRabbitMQConnection();
    }

    private void initRabbitMQConnection() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setUsername(USERNAME);
            factory.setPassword(PASSWORD);
            factory.setPort(PORT);
            factory.setVirtualHost("/");
            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void sendFriendRequest(String fromUserUsername, String toUserUsername,RequestStatus status) {
        try {
            // 创建好友请求消息
            FriendRequest request = new FriendRequest(MessageType.FRIENDREQUEST,fromUserUsername, toUserUsername,status);
            String json = JSON.toJSONString(request);

            // 发送好友请求消息到 RabbitMQ 队列
            channel.basicPublish("", QUEUE_NAME, null, json.getBytes(StandardCharsets.UTF_8));
            System.out.println("Sent friend request: " + json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}