package com.example.chatui.aboutFriend;

import com.example.chatui.ChatApp;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class RequestFriendCell extends ListCell<RequestFriend> {
    public HBox hBox = new HBox(10);
    private Circle avatarCircle = new Circle(30); // 圆形头像，半径为 25
    private ImageView avatarImageView = new ImageView();
    private Text nameText = new Text();
    public HBox rightBox=new HBox();
    public HBox leftBox=new HBox(5);

    private static final String acceptedDescription="Accepted";
    private static final String requestedDescription="Requested";
    private static final String pendingDescription="Pending";

    private Button addFriendButton=new Button();
    private Button rejectFriendButton = new Button();

    public RequestFriendCell() {
        super();
        // 将头像视图设置为与圆形相同的大小
        hBox.setPrefWidth(300);
        avatarCircle.setCenterX(30);
        avatarCircle.setCenterY(30);
        avatarImageView.setFitWidth(60);
        avatarImageView.setFitHeight(60);
        avatarImageView.setClip(avatarCircle); // 使用 Circle 作为裁剪形状
        hBox.setStyle("-fx-background-color: transparent;" + // 背景色
                "-fx-padding: 10px;" +              // 内边距
                "-fx-background-radius: 15px;");     // 圆角半径

        nameText.setStyle(  "-fx-font-weight: bold;");
        leftBox.setAlignment(Pos.CENTER_LEFT);
        // 将头像和名称放在左侧 HBox 中
        leftBox.getChildren().addAll(avatarImageView, nameText);
        //右侧面板放按钮
        addFriendButton.setFocusTraversable(false);
        addFriendButton.getStyleClass().add("friend-button");
        rejectFriendButton.getStyleClass().add("friend-button");
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setPrefWidth(hBox.getPrefWidth()-leftBox.getPrefWidth());
    }

    @Override
    protected void updateItem(RequestFriend requestFriend, boolean empty) {
        super.updateItem(requestFriend, empty);
        if (empty || requestFriend == null) {
            setGraphic(null);
        } else {
            String relation=requestFriend.getRequestStatus();
            rightBox.getChildren().clear();
            switch (relation) {
                //我方决定中
                case (pendingDescription) -> {
                    addFriendButton.setText("接受");
                    rejectFriendButton.setText("拒绝");
                    rightBox.getChildren().addAll(addFriendButton,rejectFriendButton);
                }
                //我方接受请求
                case (acceptedDescription) -> {
                    addFriendButton.setText("已接受");
                    addFriendButton.setDisable(true);
                    rightBox.getChildren().add(addFriendButton);
                }
                //我方清琼中
                case(requestedDescription)->{
                    addFriendButton.setText("消息已发送");
                    rightBox.setDisable(true);
                    rightBox.getChildren().add(addFriendButton);
                }
                default -> {
                }
            }

            avatarImageView.setImage(requestFriend.getAvatar());
            nameText.setText(requestFriend.getUsername());
            setGraphic(hBox);
            setStyle("-fx-background-color: transparent;"); // 确保ListCell背景透明
            if(requestFriend.equals(ChatApp.chosenRequestFriend)){
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
