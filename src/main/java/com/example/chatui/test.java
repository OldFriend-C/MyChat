package com.example.chatui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class test extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) {
        Button openDialogButton = new Button("打开对话框");
        openDialogButton.setOnAction(e -> showDialog());

        StackPane primaryLayout = new StackPane(openDialogButton);
        primaryStage.setScene(new Scene(primaryLayout, 300, 200));
        primaryStage.setTitle("主窗口");
        primaryStage.show();
    }

    private void showDialog() {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle("透明对话框");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-radius: 20;-fx-border-radius: 20");

        Scene scene = new Scene(root, 300, 200);
        scene.setFill(Color.TRANSPARENT);

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            dialog.setX(event.getScreenX() - xOffset);
            dialog.setY(event.getScreenY() - yOffset);
        });

        dialog.setScene(scene);
        dialog.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}