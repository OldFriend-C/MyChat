package com.example.chatui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class test extends Application {

    private VBox chatBox;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("聊天输入框");

        BorderPane layout = new BorderPane();
        chatBox = new VBox(); // 用于显示聊天记录

        // 创建文本输入框
        TextArea inputArea = new TextArea();
        inputArea.setPromptText("输入消息...");
        inputArea.setWrapText(true);

        // 创建发送按钮
        Button sendButton = new Button("发送");
        sendButton.setOnAction(e -> {
            String message = inputArea.getText();
            if (!message.trim().isEmpty()) {
                sendMessage(message); // 发送文本消息
                inputArea.clear(); // 清空输入框
            }
        });

        // 创建发送表情按钮
        Button emojiButton = new Button("选择表情");
        emojiButton.setOnAction(e -> openEmojiSelector(inputArea));

        // 设置布局
        layout.setCenter(chatBox);
        layout.setBottom(inputArea);
        layout.setLeft(emojiButton);
        layout.setRight(sendButton);

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendMessage(String message) {
        // 处理文本消息发送逻辑
        TextArea messageArea = new TextArea(message);
        messageArea.setEditable(false);
        chatBox.getChildren().add(messageArea);
        System.out.println("发送消息: " + message);
    }

    private void openEmojiSelector(TextArea inputArea) {
        // 简单的表情选择器示例
        Stage emojiStage = new Stage();
        VBox emojiBox = new VBox();

        // 添加表情按钮
        String[] emojis = {"😀", "😂", "😍", "😢", "😡"}; // 示例表情
        for (String emoji : emojis) {
            Button emojiButton = new Button(emoji);
            emojiButton.setOnAction(e -> {
                inputArea.appendText(emoji); // 将表情插入输入框
                emojiStage.close(); // 关闭表情选择器
            });
            emojiBox.getChildren().add(emojiButton);
        }

        Scene emojiScene = new Scene(emojiBox, 200, 150);
        emojiStage.setScene(emojiScene);
        emojiStage.setTitle("选择表情");
        emojiStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
