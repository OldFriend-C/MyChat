package com.example.chatui.MQChat;

import com.alibaba.fastjson.JSON;
import com.example.chatui.aboutFriend.RequestRecord;
import com.example.chatui.aboutMessage.InfoType;
import com.example.chatui.friendRequest.FriendRequest;
import com.example.chatui.friendRequest.RequestStatus;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import javafx.scene.image.Image;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import static com.example.chatui.ChatApp.requestUsers;
import static com.example.chatui.ChatApp.updateSilderBar;
import static com.example.chatui.basic.LoginBasicTool.processFriendList;

public class SendFriendRequestClient {
    private static final String QUEUE_NAME = "friendRequest";
    private static final String HOST="localhost";
    private static final int PORT=5672;
    private static final String USERNAME="remote";
    private static final String PASSWORD="123456";
    private static final String VIRTUAL_HOST="/";

    private Connection connection;
    private Channel channel;

    public SendFriendRequestClient() {
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

    public void sendFriendRequest(String fromUserUsername, String toUserUsername, RequestStatus status, Image toUseravatar) {
        try {
            // 创建好友请求消息
            FriendRequest request = new FriendRequest(InfoType.FRIENDREQUEST,fromUserUsername, toUserUsername,status);
            String requestJson = JSON.toJSONString(request);
            // 发送好友请求消息到 RabbitMQ 队列
            channel.basicPublish("", QUEUE_NAME, null, requestJson.getBytes(StandardCharsets.UTF_8));
            System.out.println("Sent friend request: " + requestJson);
            processSendRequest(request,toUseravatar);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processSendRequest(FriendRequest request,Image avatar) {
        processSendSliderBar(request,avatar);
        if(request.getStatus()==RequestStatus.ACCEPTED){
            processFriendList(request,avatar);
        }
    }

    private void processSendSliderBar(FriendRequest request,Image avatar){
        boolean flag=false;
        for(RequestRecord requestRecord: requestUsers){
            if(Objects.equals(requestRecord.getUsername(), request.getToUserUsername())) {
                requestRecord.setRequestStatus(request.getStatus().getDescription());
                flag=true;
                break;
            }
        }
        if(!flag){
            if(request.getStatus()==RequestStatus.REQUESTED){
                RequestRecord requestRecord=new RequestRecord();
                requestRecord.setUsername(request.getToUserUsername());
                requestRecord.setRequestStatus(request.getStatus().getDescription());
                requestRecord.setAvatar(avatar);
                requestRecord.setRequestTime(new Date());
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
            System.out.println("关闭出现错误");
        }
    }
}