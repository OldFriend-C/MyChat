package com.example.chatui.MQChat;

import com.rabbitmq.client.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ChatClient {
    private static final String HOST = "192.168.1.108"; // RabbitMQ 服务的主机
    private static final int PORT = 5672; // RabbitMQ 默认端口
    private static final String USERNAME = "remote";
    private static final String PASSWORD = "123456"; // RabbitMQ 密码
    private String username; // 当前用户的用户名
    private Connection connection;
    private static final String virtualHost="/";
    private Channel channel;

    public ChatClient(String username) {
        this.username = username;
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

            // 为用户创建和订阅一个以用户名命名的队列
            String queueName = username;
            channel.queueDeclare(queueName, true, false, false, null);
            System.out.println(" [*] Waiting for messages in queue: " + queueName);

            // TODO:接收好友请求后通过侧边栏展现好友请求
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message=delivery.getBody().toString();
                // 确保在 JavaFX 应用线程中显示对话框
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("新消息");
                    alert.setHeaderText(null); // 可选设置
                    alert.setContentText("收到消息: " + message);
                    alert.showAndWait(); // 显示对话框
                });

            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

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