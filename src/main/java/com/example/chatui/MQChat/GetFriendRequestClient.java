package com.example.chatui.MQChat;

import com.alibaba.fastjson.JSONObject;
import com.example.chatui.aboutFriend.RequestRecord;
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
import static com.example.chatui.basic.LoginBasicTool.avatarBae64ToImage;
import static com.example.chatui.basic.LoginBasicTool.processFriendList;

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
                    byte[] message=delivery.getBody();
                    String request= new String(message, StandardCharsets.UTF_8);
                    JSONObject requestJson= JSONObject.parseObject(request);

                    RequestRecord requestRecord =new RequestRecord();
                    requestRecord.setRequestTime(requestJson.getDate("requestTime"));
                    requestRecord.setAvatar(avatarBae64ToImage(requestJson.getString("avatar")));
                    requestRecord.setUsername(requestJson.getString("username"));
                    requestRecord.setRequestStatus(requestJson.getString("requestStatus"));
                    processRequest(requestRecord);
                });
            };
            channel.basicConsume(friendRequestQueueName, true, deliverCallback, consumerTag -> { });

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void processRequest(RequestRecord requestRecord){
        processSilderBar(requestRecord);
        if(Objects.equals(requestRecord.getRequestStatus(), RequestStatus.ACCEPTED.getDescription())){
            processFriendList(requestRecord);
        }
    }

    private void processSilderBar(RequestRecord requestRecord){
        boolean flag=false;
        for(RequestRecord Record: requestUsers){
            if(Record.getUsername().equals(requestRecord.getUsername())){
                if(requestRecord.getRequestStatus().equals(RequestStatus.REJECT.getDescription())){
                    Record.setRequestStatus(RequestStatus.DECLINED.getDescription());
                }
                else if(requestRecord.getRequestStatus().equals(RequestStatus.REQUESTED.getDescription())){
                    Record.setRequestStatus(RequestStatus.PENDING.getDescription());
                }
                else{
                    Record.setRequestStatus(requestRecord.getRequestStatus());
                }
                flag=true;
                break;
            }
        }
        if(!flag){
            if(Objects.equals(requestRecord.getRequestStatus(), RequestStatus.REQUESTED.getDescription())){
                requestRecord.setRequestStatus(RequestStatus.PENDING.getDescription());
                requestUsers.add(requestRecord);
            }
        }
        updateSilderBar();
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