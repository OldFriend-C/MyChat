package com.example.chatui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class test extends Application {

    private boolean isPasswordVisible = false; // 控制密码显示状态

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("密码框明文暗文切换");

        VBox vbox = new VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));

        // 用户名框
        TextField userNameField = new TextField();
        userNameField.setPromptText("用户名");
        vbox.getChildren().add(userNameField);

        // 密码框和按钮
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("密码");

        TextField passwordTextField = new TextField();
        passwordTextField.setPromptText("密码");
        passwordTextField.setVisible(false); // 初始时隐藏明文框

        Button toggleButton = new Button("显示");
        toggleButton.setOnAction(event -> {
            isPasswordVisible = !isPasswordVisible; // 切换状态
            if (isPasswordVisible) {
                passwordTextField.setText(passwordField.getText()); // 复制密码
                passwordField.setVisible(false); // 隐藏密码框
                passwordTextField.setVisible(true); // 显示明文框
                toggleButton.setText("隐藏"); // 更新按钮文本
            } else {
                passwordField.setText(passwordTextField.getText()); // 复制明文
                passwordField.setVisible(true); // 显示密码框
                passwordTextField.setVisible(false); // 隐藏明文框
                toggleButton.setText("显示"); // 更新按钮文本
            }
        });

        HBox passwordBox = new HBox(passwordField, passwordTextField, toggleButton);
        vbox.getChildren().add(passwordBox);

        // 登录按钮
        Button loginButton = new Button("登录");
        vbox.getChildren().add(loginButton);

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
