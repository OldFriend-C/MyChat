package com.example.chatui.MQChat;

import com.alibaba.fastjson.JSON;
import com.example.chatui.aboutMessage.Message;
import com.example.chatui.aboutMessage.MessageType;
import com.example.chatui.aboutMessage.SendMsg;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import static com.example.chatui.ChatApp.*;
import static com.example.chatui.LoginApp.nowUser;

public class  SendMessageClient{
    private static final String QUEUE_NAME = "message";
    private static final String HOST="localhost";
    private static final int PORT=5672;
    private static final String USERNAME="remote";
    private static final String PASSWORD="123456";

    private static final String VIRTUAL_HOST="/";
    private Connection connection;
    private Channel channel;

    public SendMessageClient() {
        initRabbitMQConnection();
    }

    private void initRabbitMQConnection() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setUsername(USERNAME);
            factory.setPassword(PASSWORD);
            factory.setPort(PORT);
            factory.setVirtualHost(VIRTUAL_HOST);
            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(SendMsg message) {
        try {
            String messageJson = JSON.toJSONString(message);
            //前端显示消息发送
            displayMessage(message);
            // 发送消息到 RabbitMQ 队列
            channel.basicPublish("", QUEUE_NAME, null, messageJson.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayMessage(SendMsg message) {
        if(Objects.equals(message.getMessageType(), MessageType.FILE.getDescription()))
            return;

        Message newMessage=new Message(nowUser,chosenUser, MessageType.TEXT.getDescription(),message.getMessageContent(),message.getCreatedAt());

        saveMessageListView.get(chosenUser).add(newMessage);
        messageListView.getItems().add(newMessage);
        //自动滚到最后一行
        messageListView.scrollTo(messageListView.getItems().size()-1);
    }


    public void close() {
        try {
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            System.out.println("关闭出现错误");
        }
    }
}