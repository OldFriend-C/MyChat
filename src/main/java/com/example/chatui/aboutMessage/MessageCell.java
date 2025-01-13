package com.example.chatui.aboutMessage;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class MessageCell extends ListCell<Message> {
    private HBox hBox = new HBox(10);
    private VBox vBox = new VBox(5);
    private VBox info=new VBox(5);
    private Circle avatarCircle = new Circle(20);
    private ImageView avatarImageView = new ImageView();
    private Text sendernameText = new Text();
    private Text messageText = new Text();


    @Override
    protected void updateItem(Message chatMessage, boolean empty) {
        super.updateItem(chatMessage, empty);

        // 清除之前的内容
        hBox.getChildren().clear();
        vBox.getChildren().clear();
        info.getChildren().clear();
        if (empty || chatMessage == null) {
            avatarImageView.setImage(null);
            sendernameText.setText("");
            messageText.setText("");
            setGraphic(null);
        } else {
            try {
                avatarImageView.setImage(chatMessage.getAvatar());
            } catch (Exception e) {
                avatarImageView.setImage(new Image("file:avatar/124887685_p0.png"));
            }
            sendernameText.setText(chatMessage.getSenderName());
            messageText.setText(chatMessage.getMessage());
            sendernameText.setStyle("-fx-font-weight: bold;");

            // 确保文本能够换行
            if (messageText.getLayoutBounds().getWidth() > 600) {
                messageText.setWrappingWidth(600);
            }
            if (sendernameText.getLayoutBounds().getWidth() > 600) {
                sendernameText.setWrappingWidth(600);
            }
            messageText.setStyle("-fx-font-size: 14;");

            avatarCircle.setCenterX(20);
            avatarCircle.setCenterY(20);
            avatarImageView.setFitWidth(40);
            avatarImageView.setFitHeight(40);
            avatarImageView.setClip(avatarCircle);

            // 使用HBox包裹消息文本以实现气泡效果
            StackPane messageContainer = new StackPane();
            messageContainer.setStyle("-fx-background-color: #0099ff; " +
                    "-fx-padding: 10px; " +
                    "-fx-background-radius: 15px;" +
                    "-fx-border-color: transparent;");

            messageContainer.getChildren().addAll(messageText);
            messageContainer.setAlignment(Pos.TOP_LEFT);

            hBox.setStyle("-fx-background-color: transparent;" +
                    "-fx-padding: 10px;");

            info.getChildren().addAll(sendernameText,messageContainer);
            hBox.getChildren().addAll(avatarImageView, info);
            vBox.getChildren().add(hBox);
            setGraphic(vBox);
            setStyle("-fx-background-color: transparent;");
        }
    }

}