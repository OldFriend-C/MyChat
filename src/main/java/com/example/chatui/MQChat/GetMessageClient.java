package com.example.chatui.MQChat;

import com.alibaba.fastjson.JSONObject;
import com.example.chatui.aboutFriend.RequestRecord;
import com.example.chatui.aboutMessage.Message;
import com.example.chatui.aboutMessage.SendMsg;
import com.example.chatui.aboutUser.User;
import com.example.chatui.friendRequest.RequestStatus;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import javafx.application.Platform;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import static com.example.chatui.ChatApp.*;
import static com.example.chatui.LoginApp.*;
import static com.example.chatui.basic.LoginBasicTool.*;

public class GetMessageClient {
    private static final String HOST = "localhost"; // RabbitMQ 服务的主机
    private static final int PORT = 5672; // RabbitMQ 默认端口
    private static final String USERNAME = "remote";
    private static final String PASSWORD = "123456"; // RabbitMQ 密码
    private Connection connection;
    private static final String VIRTUAL_HOST="/";
    private Channel channel;

    public GetMessageClient() {
        String messsageQueueName = nowUsername + "/message";
        try {
            // 创建与 RabbitMQ 的连接
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setPort(PORT);
            factory.setUsername(USERNAME);
            factory.setPassword(PASSWORD);
            factory.setVirtualHost(VIRTUAL_HOST); // 虚拟主机
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(messsageQueueName, true, false, false, null);
            System.out.println(" [*] Waiting for message messages in queue: " + messsageQueueName);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Platform.runLater(()->{
                    byte[] messagebyte=delivery.getBody();
                    String message= new String(messagebyte, StandardCharsets.UTF_8);
                    SendMsg getmessage= JSONObject.parseObject(message, SendMsg.class);
                    User sender=new User(getmessage.getSenderUsername(),saveUserAvatar.get(getmessage.getSenderUsername()));
                    Message msg=new Message(sender,nowUser,getmessage.getMessageType(),getmessage.getMessageContent(),getmessage.getCreatedAt());
                    setSaveMessageList(msg);
                    messageListView.scrollTo(messageListView.getItems().size()-1);
                });
            };
            channel.basicConsume(messsageQueueName, true, deliverCallback, consumerTag -> { });

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