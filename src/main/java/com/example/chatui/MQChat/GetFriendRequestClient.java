package com.example.chatui.MQChat;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import javafx.application.Platform;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.example.chatui.ChatApp.requestUsers;
import static com.example.chatui.ChatApp.updateSilderBar;
import static com.example.chatui.basic.LoginBasicTool.loadRequest;

public class GetFriendRequestClient {
    private static final String HOST = "localhost"; // RabbitMQ 服务的主机
    private static final int PORT = 5672; // RabbitMQ 默认端口
    private static final String USERNAME = "remote";
    private static final String PASSWORD = "123456"; // RabbitMQ 密码
    private Connection connection;
    private static final String virtualHost="/";
    private Channel channel;
    private final String friendRequestQueueName;

    public GetFriendRequestClient(String username) {
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
            System.out.println(" [*] Waiting for friend request messages in queue: " + friendRequestQueueName);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Platform.runLater(()->{
                    requestUsers= loadRequest();
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