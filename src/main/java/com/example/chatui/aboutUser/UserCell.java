package com.example.chatui.aboutUser;

import com.example.chatui.ChatApp;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class UserCell extends ListCell<User> {
    private HBox hBox = new HBox(10);
    private Circle avatarCircle = new Circle(30); // 圆形头像，半径为 25
    private ImageView avatarImageView = new ImageView();
    private Text nameText = new Text();

    public UserCell() {
        super();

        // 将头像视图设置为与圆形相同的大小
        avatarCircle.setCenterX(30);
        avatarCircle.setCenterY(30);
        avatarImageView.setFitWidth(60);
        avatarImageView.setFitHeight(60);
        avatarImageView.setClip(avatarCircle); // 使用 Circle 作为裁剪形状
        hBox.setStyle("-fx-background-color: transparent;" + // 背景色
                "-fx-padding: 10px;" +              // 内边距
                "-fx-background-radius: 15px;");     // 圆角半径

        nameText.setStyle(  "-fx-font-weight: bold;");


        hBox.getChildren().addAll(avatarImageView, nameText);
        setGraphic(hBox);
    }

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        if (empty || user == null) {
            setGraphic(null);
        } else {
            avatarImageView.setImage(user.getAvatar());
            nameText.setText(user.getName());
            setGraphic(hBox);
            setStyle("-fx-background-color: transparent;"); // 确保ListCell背景透明
            if(user.equals(ChatApp.chosenUser)){
                hBox.setStyle("-fx-background-color: #e8eaf1;" +
                        "-fx-padding: 10px;" +
                        "-fx-background-radius: 15px;");
            }
            else{
                hBox.setStyle("-fx-background-color: transparent;" +
                        "-fx-padding: 10px;" +
                        "-fx-background-radius: 15px;");
            }
        }
    }
}
