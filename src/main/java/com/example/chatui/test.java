package com.example.chatui;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class test extends Application {

    private StackPane rootPane;
    private VBox sidebarPane;
    private boolean isSidebarVisible = false;

    @Override
    public void start(Stage primaryStage) {
        // 创建根 StackPane
        rootPane = new StackPane();

        // 创建主内容区域
        BorderPane mainPane = new BorderPane();
        Label mainLabel = new Label("Main Content");
        mainPane.setCenter(mainLabel);

        // 创建侧边栏窗口
        sidebarPane = new VBox();
        sidebarPane.setStyle("-fx-background-color: #d22222; -fx-padding: 20px;");
        sidebarPane.getChildren().addAll(
                new Label("Sidebar Content"),
                new Button("Option 1"),
                new Button("Option 2"),
                new Button("Close")
        );
        sidebarPane.setMaxWidth(200);

        // 将主内容区域和侧边栏窗口添加到 StackPane 中
        rootPane.getChildren().addAll(sidebarPane, mainPane);

        // 设置侧边栏窗口的初始位置
        sidebarPane.setTranslateX(-400);

        // 添加显示/隐藏侧边栏的交互逻辑
        Button toggleSidebarButton = new Button("Toggle Sidebar");
        toggleSidebarButton.setOnAction(event -> toggleSidebar());
        HBox bottomPane = new HBox(toggleSidebarButton);
        mainPane.setBottom(bottomPane);

        // 创建场景并显示
        Scene scene = new Scene(rootPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sidebar Popup Example");
        primaryStage.show();
    }

    private void toggleSidebar() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebarPane);
        if (isSidebarVisible) {
            transition.setToX(-300);
        } else {
            transition.setToX(-250);
        }
        transition.play();
        isSidebarVisible = !isSidebarVisible;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
