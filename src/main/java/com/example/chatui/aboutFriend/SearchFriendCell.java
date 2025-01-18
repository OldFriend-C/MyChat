package com.example.chatui.aboutFriend;

import com.example.chatui.ChatApp;
import com.example.chatui.friendRequest.RequestStatus;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.Date;

import static com.example.chatui.ChatApp.requestUsers;
import static com.example.chatui.ChatApp.updateSilderBar;
import static com.example.chatui.LoginApp.nowUsername;
import static com.example.chatui.LoginApp.sendFriendRequestClient;

public class SearchFriendCell extends ListCell<SearchFriend> {
    public HBox hBox = new HBox(10);
    private Circle avatarCircle = new Circle(30); // 圆形头像，半径为 25
    private ImageView avatarImageView = new ImageView();
    private Text nameText = new Text();
    public HBox rightBox=new HBox();
    public HBox leftBox=new HBox(5);

    private static final String irrelevantDescription="Irrelevant";
    private static final String acceptedDescription="Accepted";
    private static final String requestedDescription="Requested";
    private static final String pendingDescription="Pending";
    private static final String rejectDescription ="Reject";
    private static final String  declinedDescription="Declined";

    public Button addFriendButton = new Button("添加好友");

    public SearchFriendCell() {
        super();
        // 将头像视图设置为与圆形相同的大小
        hBox.setPrefWidth(600);
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
        //右侧面板
        addFriendButton.setFocusTraversable(false);
        addFriendButton.getStyleClass().add("add-friend-button");
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setPrefWidth(hBox.getPrefWidth()-leftBox.getPrefWidth());
    }

    @Override
    protected void updateItem(SearchFriend searchFriend, boolean empty) {
        super.updateItem(searchFriend, empty);
        if (empty || searchFriend == null) {
            setGraphic(null);
        } else {
            String relation=searchFriend.getRequestStatus();
            rightBox.getChildren().clear();
            switch (relation) {
                case (irrelevantDescription), (declinedDescription), (rejectDescription) -> {
                    setSearchFriend(searchFriend);
                }
                case (acceptedDescription) -> {
                    addFriendButton.setText("已成为好友");
                    addFriendButton.setDisable(true);
                    rightBox.getChildren().add(addFriendButton);
                }
                case (requestedDescription) -> {
                    addFriendButton.setText("消息已发送");
                    addFriendButton.setDisable(true);
                    rightBox.getChildren().add(addFriendButton);
                }
                case(pendingDescription)->{
                    addFriendButton.setText("等待处理请求");
                    addFriendButton.setDisable(true);
                    rightBox.getChildren().add(addFriendButton);
                }
                default -> {
                }
            }

            avatarImageView.setImage(searchFriend.getAvatar());
            nameText.setText(searchFriend.getUsername());
            setGraphic(hBox);
            setStyle("-fx-background-color: transparent;"); // 确保ListCell背景透明
            if(searchFriend.equals(ChatApp.chosenSearchFriend)){
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
    public void setSearchFriend(SearchFriend searchFriend){
        addFriendButton.setOnMouseClicked(e -> {
            sendFriendRequestClient.sendFriendRequest(nowUsername, searchFriend.getUsername(), RequestStatus.REQUESTED,searchFriend.getAvatar()); //发送好友请求
            //将按钮改为请求已发送的文字，并且让按钮不可按
            addFriendButton.setText("请求已发送");
            addFriendButton.setDisable(true);
        });
        rightBox.getChildren().add(addFriendButton);
    }


}
