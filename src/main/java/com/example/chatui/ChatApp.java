package com.example.chatui;

import com.example.chatui.aboutUser.User;
import com.example.chatui.aboutUser.UserCell;
import com.example.chatui.aboutMessage.Message;
import com.example.chatui.aboutMessage.MessageCell;
import com.example.chatui.basic.LoginBasicTool;
import com.example.chatui.basic.NoSelectionModel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;

import static com.example.chatui.LoginApp.avatar;
import static com.example.chatui.basic.LoginBasicTool.getFriendsList;

public class ChatApp extends Application {

    public static User chosenUser;
    private static VBox functionPlace=new VBox();
    private static VBox contentPlace=new VBox();
    private static VBox chatPlace=new VBox();
    private static List<User> friendsList;


    @Override
    public void start(Stage primaryStage) {
        // 隐藏自带的标题栏
        primaryStage.initStyle(StageStyle.UNDECORATED);

        // 创建自定义标题栏
        HBox titleBar = LoginBasicTool.creatChatTitle(primaryStage);


        // 创建左侧按钮列表
        VBox leftPane = functionPane();

        // 创建中间成员列表
        VBox centerPane = contentPane();

        // 创建右侧聊天界面
        VBox rightPane = chatPane();

        // 创建整个界面的根节点
        HBox root = new HBox(leftPane, centerPane, rightPane);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(0);
        root.setPadding(new Insets(0));
        root.setStyle("-fx-background-color: #FFFFFF;");
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        // 设置背景为全白色
        Scene scene = new Scene(new VBox(titleBar, root), 1200, 800);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat App");
        primaryStage.show();

    }

        //最左侧面板
    private VBox functionPane() {
        functionPlace.setAlignment(Pos.TOP_CENTER);
        functionPlace.setSpacing(10);
        functionPlace.setMaxWidth(100);
        functionPlace.setPrefHeight(800);
        functionPlace.setStyle("-fx-background-color: #FFFFFF;");
        VBox.setVgrow(functionPlace, Priority.ALWAYS);


        // 添加按钮
        ImageView friendsIcon = new ImageView(new Image("file:icons/friends.png"));
        friendsIcon.setFitWidth(25);
        friendsIcon.setFitHeight(25);
        Button friendsButton = new Button();
        friendsButton.setGraphic(friendsIcon);
        friendsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #d00a0a;");
        friendsButton.setOnMouseEntered(e -> {
            friendsButton.setStyle("-fx-background-color: #e1e1e1; -fx-text-fill: #e1e1e1;");
        });
        friendsButton.setOnMouseExited(e -> {
            friendsButton.setStyle("-fx-background-color: #e1e1e1; -fx-text-fill: #e1e1e1;");
        });

        // 点击事件处理
        friendsButton.setOnAction(event -> {
            // 创建 ColorAdjust 对象以调整颜色
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(1.5); // 轻微调整色调，使其接近紫色
            colorAdjust.setSaturation(0.5); // 中等饱和度
            colorAdjust.setBrightness(0); // 保持亮度不变
            friendsIcon.setEffect(colorAdjust); // 将效果应用到图标上
        });

        functionPlace.getChildren().addAll(friendsButton);
        return functionPlace;
    }

    //中间面板
    private VBox contentPane() {
        contentPlace.setAlignment(Pos.TOP_LEFT);
        contentPlace.setSpacing(10);
        contentPlace.setPrefWidth(100);
        contentPlace.setPrefHeight(800);
        contentPlace.setStyle("-fx-background-color: #FFFFFF;-fx-border-color: rgba(0, 0, 0, 0.08); -fx-border-width: 0 0 0 1;");

        friendsList=getFriendsList();//获取好友列表
        //创建好友列表
        ListView<User> friendsListView = new ListView<>();

        double cellHeight=90;

        friendsListView.setPrefHeight(friendsList.size()*cellHeight);
        friendsListView.getItems().addAll(friendsList);
        friendsListView.setCellFactory(param -> new UserCell()); // 设置自定义 Cell
        friendsListView.setStyle("-fx-background-color: transparent");
        friendsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            chosenUser = newValue; // 更新选中的用户
            updataChatName();
        });

        // 鼠标进入事件
        contentPlace.setOnMouseEntered(event -> {
            friendsListView.lookup(".scroll-bar:vertical").setVisible(true); // 显示垂直滚动条
        });

        // 鼠标退出事件
        contentPlace.setOnMouseExited(event -> {
            friendsListView.lookup(".scroll-bar:vertical").setVisible(false); // 显示垂直滚动条
        });

        // 添加成员列表
        contentPlace.getChildren().add(friendsListView);

        return contentPlace;
    }


    // 右侧面板
    private VBox chatPane() {
        chatPlace.setSpacing(10);
        chatPlace.setPrefWidth(750);
        chatPlace.setStyle("-fx-background-color: #FFFFFF;");

        //聊天对象名称
        Text chatname=new Text();

        if(chosenUser==null){
            chatname.setText("");
        }
        else{
            chatname.setText(chosenUser.getName());
        }
        chatname.setStyle("-fx-font-weight: bold; -fx-font-size: 12px");
        chatPlace.setAlignment(Pos.TOP_CENTER);
        chatPlace.getChildren().add(chatname);


        List<Message> messages = new ArrayList<>();
        ListView<Message> MessageListView = new ListView<>();
        double cellHeight = 90; // 假设每个单元格的高度为 50
        MessageListView.setPrefHeight(messages.size() * cellHeight);
        MessageListView.setPrefHeight(650);
        MessageListView.getItems().addAll(messages);
        MessageListView.setCellFactory(param -> new MessageCell()); // 设置自定义 Cell
        MessageListView.setStyle("-fx-background-color: transparent");
        MessageListView.setEditable(false);
        MessageListView.setSelectionModel(new NoSelectionModel<>());
        chatPlace.getChildren().add(MessageListView);

        //功能面板
        HBox functionPane=new HBox();
        functionPane.setAlignment(Pos.TOP_LEFT);

        Button emoji=new Button();   //发送表情按钮
        ImageView emojiIcon = new ImageView(new Image("file:icons/emoji.png"));
        emojiIcon.setFitWidth(30);
        emojiIcon.setFitHeight(30);
        emoji.setGraphic(emojiIcon);
        emoji.setStyle("-fx-background-color: transparent;");
        emoji.setOnMouseClicked(event -> {
            emojiIcon.setImage(new Image("file:icons/changedemoji.png"));
        });
        emoji.setOnMouseEntered(event -> {
            emojiIcon.setImage(new Image("file:icons/changedemoji.png"));
        });
        emoji.setOnMouseExited(event -> {
            emojiIcon.setImage(new Image("file:icons/emoji.png"));
        });

        Button fileselector=new Button();   //传输文件按钮
        ImageView fileselectorIcon = new ImageView(new Image("file:icons/file.png"));
        fileselectorIcon.setFitWidth(25);
        fileselectorIcon.setFitHeight(25);
        fileselector.setGraphic(fileselectorIcon);
        fileselector.setStyle("-fx-background-color: transparent;");
        fileselector.setOnMouseClicked(event -> {
            fileselectorIcon.setImage(new Image("file:icons/changedfile.png"));
        });
        fileselector.setOnMouseEntered(event -> {
            fileselectorIcon.setImage(new Image("file:icons/changedfile.png"));
        });
        fileselector.setOnMouseExited(event -> {
            fileselectorIcon.setImage(new Image("file:icons/file.png"));
        });
        //将按钮加入布局
        functionPane.getChildren().addAll(emoji,fileselector);
        chatPlace.getChildren().add(functionPane);



        // 发送按钮
        HBox sendButtonBox=new HBox();
        sendButtonBox.setAlignment(Pos.CENTER_RIGHT);
        // 发送按钮
        Button sendButton = new Button("发送");
        sendButton.setStyle("-fx-background-color: #0099FF; -fx-text-fill: #4DB8FF;"); // 增加字体大小
        sendButton.setMinWidth(80); // 设置最小宽度
        sendButton.setMinHeight(30); // 设置最小高度
        // 设置按钮的上下左右边距
        HBox.setMargin(sendButton, new Insets(10, 20, 10, 0)); // 上、右、下、左的边距
        sendButton.setStyle("-fx-background-color: #0099FF;-fx-text-fill: #4DB8FF");


        // 输入框
        TextArea inputArea = new TextArea();
        inputArea.setPrefHeight(250);
        inputArea.setMaxWidth(850);
        inputArea.setWrapText(true); // 自动换行
        inputArea.setPromptText("Enter Something...");



        chatPlace.getChildren().add(inputArea);
        sendButtonBox.getChildren().addAll(sendButton);



        inputArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                sendButton.setStyle("-fx-background-color: #0099FF;-fx-text-fill: #4DB8FF");
            } else {
                sendButton.setStyle("-fx-background-color: #0099FF;-fx-text-fill: #FFFFFF");
            }
        });


        sendButton.setOnMouseClicked(event -> {
            String message = inputArea.getText().trim();
            if (!message.isEmpty()) {
                sendMessage(inputArea,MessageListView);
                // 滚动到最后一条消息
                MessageListView.scrollTo(MessageListView.getItems().size() - 1);
                event.consume();
            }
        });

        inputArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage(inputArea,MessageListView);
                event.consume(); // 防止换行
                // 滚动到最后一条消息
                MessageListView.scrollTo(MessageListView.getItems().size() - 1);
            }
        });


        // 将组件添加到聊天区域
        chatPlace.getChildren().add(sendButtonBox);

        return chatPlace;
    }

    private void updataChatName() {
        Text chatname= (Text) chatPlace.getChildren().get(0);
        if(chosenUser==null){
            chatname.setText("");
        }
        else{
            chatname.setText(chosenUser.getName());
        }
    }

    private void sendMessage(TextArea inputArea,ListView<Message> messageList) {
        String message = inputArea.getText().trim();
        if (!message.isEmpty()) {
            String sendMessage=inputArea.getText().trim();
            messageList.getItems().add(new Message("you",sendMessage,avatar.getImage())); // 添加到消息列表
            inputArea.clear(); // 清空输入框
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
