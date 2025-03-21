package com.example.chatui;

import com.example.chatui.aboutFriend.RequestRecord;
import com.example.chatui.aboutFriend.SearchFriend;
import com.example.chatui.aboutMessage.Message;
import com.example.chatui.aboutMessage.MessageCell;
import com.example.chatui.aboutMessage.MessageType;
import com.example.chatui.aboutMessage.SendMsg;
import com.example.chatui.aboutUser.User;
import com.example.chatui.basic.LoginBasicTool;
import com.example.chatui.basic.NoSelectionModel;
import com.example.chatui.friendRequest.RequestStatus;
import javafx.animation.TranslateTransition;
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
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.example.chatui.LoginApp.*;
import static com.example.chatui.basic.LoginBasicTool.*;

public class ChatApp extends Application {

    public static User chosenUser;
    public static SearchFriend chosenSearchFriend;
    public static RequestRecord chosenRequestRecord;
    private static VBox functionPlace=new VBox();
    private static VBox contentPlace=new VBox();
    public static VBox chatPlace=new VBox();
    public static List<User> friendsList=new ArrayList<>();

    private static VBox sliderbar=new VBox();
    public static StackPane root=new StackPane();

    public static boolean isSidebarVisible=false;
    private static final double SCENEWIDTH=1200;
    private static final double SCENHEIGHT=800;

    public static List<RequestRecord> requestUsers=new ArrayList<>();
    public static Map<String,Image> saveUserAvatar=new HashMap<>();
    private static ListView<User> friendsListView=new ListView<>();
    private static ListView<RequestRecord> requestListView=new ListView<>();

    private static ImageView bellIcon=new ImageView();
    public static boolean isBellRedPoint;

    public static ListView<Message> messageListView=new ListView<>();
    public static Map<User,List<Message>> saveMessageListView= new HashMap<>();

    public static Map<String,Image> emojiMap=new HashMap<>();

    public static List<Message> messageList=new ArrayList<>();

    public static Button emoji =new Button();

    public static Button fileselector=new Button();

    public static TextArea inputArea=new TextArea();





    @Override
    public void start(Stage primaryStage) {
        // 隐藏自带的标题栏
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        primaryStage.setResizable(true);

        // 创建自定义标题栏
        HBox titleBar = creatChatTitle(primaryStage);


        // 创建左侧按钮列表
        VBox leftPane = functionPane();

        // 创建中间成员列表
        VBox centerPane = contentPane();

        // 创建右侧聊天界面
        VBox rightPane = chatPane(primaryStage);

        //创建侧边栏
        ConfigSidebar();


        //下方主要内容
        HBox allPane = new HBox(leftPane, centerPane, rightPane);
        allPane.setAlignment(Pos.CENTER);
        allPane.setSpacing(0);
        allPane.setPadding(new Insets(0));
        allPane.setStyle("-fx-background-color: #FFFFFF;");
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        VBox mainPane=new VBox(titleBar,allPane);


        //将全部内容加入根节点
        root.getChildren().addAll(mainPane,sliderbar);

        // 设置背景为全白色
        Scene scene = new Scene(root, SCENEWIDTH, SCENHEIGHT);
        // 设置背景为全白色
        sliderbar.setTranslateX(SCENEWIDTH/2+sliderbar.getMaxWidth()/2);
        scene.getStylesheets().add(LoginBasicTool.class.getResource("/com/example/chatui/styles.css").toExternalForm());

        primaryStage.setScene(scene);

        if(!friendsList.isEmpty()){
            // 自动选择第一个用户
            friendsListView.getSelectionModel().selectFirst(); // 选择第一个用户
        }


        //添加关闭侧边栏
        sliderbar.getScene().setOnMouseClicked(event -> {
            if (!sliderbar.getBoundsInParent().contains(event.getSceneX(), event.getSceneY()) && isSidebarVisible) {
                toggleSidebar();
            }
        });

        //设置按钮的可用性
        primaryStage.getScene().getWindow().setOnShown(e->{
            if(chosenUser==null){
                emoji.setDisable(true);
                fileselector.setDisable(true);
                inputArea.setDisable(true);
            }
            else{
                emoji.setDisable(false);
                fileselector.setDisable(false);
                inputArea.setDisable(false);
            }
        });

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
        friendsListView = new ListView<>();
        friendsListView.getItems().addAll(friendsList);
        configureUserListView(friendsListView);
        // 添加成员列表
        contentPlace.getChildren().add(friendsListView);
        return contentPlace;
    }


    // 右侧面板
    private VBox chatPane(Stage primaryStage) {
        chatPlace.setSpacing(10);
        chatPlace.setPrefWidth(750);
        chatPlace.setStyle("-fx-background-color: #FFFFFF;");

        //聊天对象名称
        Text chatname=new Text();

        if(chosenUser==null){
            chatname.setText("");
        }
        else{
            chatname.setText(chosenUser.getUsername());
        }

        chatname.setStyle("-fx-font-weight: bold; -fx-font-size: 12px");
        chatPlace.setAlignment(Pos.TOP_CENTER);
        chatPlace.getChildren().add(chatname);


        messageListView.setPrefHeight(650);
        messageListView.getItems().addAll(messageList);
        messageListView.setCellFactory(param -> new MessageCell()); // 设置自定义 Cell
        messageListView.setStyle("-fx-background-color: transparent");
        messageListView.setEditable(false);
        messageListView.setSelectionModel(new NoSelectionModel<>());

        showMessageList(chosenUser);
        chatPlace.getChildren().add(messageListView);

        messageListView.setOnMouseClicked(mouseEvent ->
        {
            if(isSidebarVisible){
                toggleSidebar();
            }
        });
        // 输入框
        inputArea = new TextArea();
        inputArea.setStyle("-fx-font-size: 16;");
        inputArea.setPrefHeight(250);
        inputArea.setMaxWidth(850);
        inputArea.setWrapText(true); // 自动换行
        inputArea.setPromptText("Enter Something...");



        //功能面板
        HBox functionPane=new HBox();
        functionPane.setAlignment(Pos.TOP_LEFT);

        //保存所有表情图片
        initEmojis();
//        emojiMap=OSSUtils.getEmojis();

        emoji=new Button();   //发送表情按钮
        ImageView emojiIcon = new ImageView(new Image("file:icons/emoji.png"));
        emojiIcon.setFitWidth(30);
        emojiIcon.setFitHeight(30);
        emoji.setGraphic(emojiIcon);
        emoji.setStyle("-fx-background-color: transparent;");
        emoji.setOnMouseClicked(event -> {
            emojiIcon.setImage(new Image("file:icons/changedemoji.png"));
            ConfigEmojiTable(primaryStage,inputArea);
        });
        emoji.setOnMouseEntered(event -> {
            emojiIcon.setImage(new Image("file:icons/changedemoji.png"));
        });
        emoji.setOnMouseExited(event -> {
            emojiIcon.setImage(new Image("file:icons/emoji.png"));
        });



        fileselector=new Button();   //传输文件按钮
        ImageView fileselectorIcon = new ImageView(new Image("file:icons/file.png"));
        fileselectorIcon.setFitWidth(25);
        fileselectorIcon.setFitHeight(25);
        fileselector.setGraphic(fileselectorIcon);
        fileselector.setStyle("-fx-background-color: transparent;");
        fileselector.setOnMouseClicked(event -> {
            fileselector.setOnMouseClicked(e-> handleFileUpload(primaryStage));  //上传文件
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

        //将输入框添加布局
        chatPlace.getChildren().add(inputArea);


        // 发送按钮
        HBox sendButtonBox=new HBox();
        sendButtonBox.setAlignment(Pos.CENTER_RIGHT);
        Button sendButton = new Button("发送");
        sendButton.setStyle("-fx-background-color: #0099FF; -fx-text-fill: #4DB8FF;"); // 增加字体大小
        sendButton.setMinWidth(80); // 设置最小宽度
        sendButton.setMinHeight(30); // 设置最小高度
        // 设置按钮的上下左右边距
        HBox.setMargin(sendButton, new Insets(10, 20, 10, 0)); // 上、右、下、左的边距
        sendButton.setStyle("-fx-background-color: #0099FF;-fx-text-fill: #4DB8FF");
        sendButtonBox.getChildren().add(sendButton);

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
                SendMsg sendmsg=new SendMsg(nowUsername,chosenUser.getUsername(), MessageType.TEXT.getDescription(),message,new Date());
                sendMessage(inputArea,sendmsg);
            }
        });

        inputArea.setOnKeyPressed(event -> {
            String message = inputArea.getText().trim();
            if (event.getCode() == KeyCode.ENTER) {
                SendMsg sendmsg=new SendMsg(nowUsername,chosenUser.getUsername(), MessageType.TEXT.getDescription(),message,new Date());
                sendMessage(inputArea,sendmsg);
            }
        });


        // 将组件添加到聊天区域
        chatPlace.getChildren().add(sendButtonBox);
        return chatPlace;
    }

    private void initEmojis() {
        String directoryPath = "emojiPictures";  // 例如 "C:\\Users\\YourUsername\\Documents"

        try {
            // 使用Files.list()方法获取文件夹下的所有文件
            List<Path> fileList = Files.list(Paths.get(directoryPath)).toList();

            // 遍历文件列表并打印文件名
            for (Path path : fileList) {
                String filename=path.getFileName().toString();
                String emojiId=filename.substring(0,filename.lastIndexOf('.'));
                emojiMap.put("["+emojiId+"]",new Image("file:"+directoryPath+"/"+emojiId+".png"));
            }
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }
    }


    private void sendMessage(TextArea inputArea,SendMsg message) {
        if (message!=null) {
            inputArea.clear();
            sendMessageClient.sendMessage(message);

        }
    }

    //创建侧边栏
    private void ConfigSidebar() {
        sliderbar = new VBox();
        sliderbar.setId("sidebar");
        sliderbar.setAlignment(Pos.TOP_CENTER);
        sliderbar.setSpacing(10);
        sliderbar.setMaxWidth(380);
        sliderbar.setMaxHeight(780);
        sliderbar.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20;-fx-background-radius: 15;-fx-border-radius: 15");
        requestUsers=loadRequest();
        updateSilderBar();
        sliderbar.getChildren().add(requestListView);
        // ...
    }


    private void toggleSidebar() {
        chosenRequestRecord =null;
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sliderbar);
        if(isSidebarVisible){
            transition.setToX(SCENEWIDTH/2+sliderbar.getMaxWidth()/2);
            transition.play();
            isSidebarVisible=!isSidebarVisible;
        }
        else {
            transition.setToX(SCENEWIDTH / 2 - sliderbar.getMaxWidth() / 2);
            transition.play();
            isSidebarVisible = !isSidebarVisible;
        }
    }



    public  HBox creatChatTitle(Stage primaryStage) {
        HBox titleBar = new HBox();
        avatar.setOnMouseClicked(null);  //清除上传头像的事件监听
        avatar.setOnMouseClicked(e->uploadAvatar(primaryStage,true)); //添加可以更改头像的监听事件

        titleBar.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 15;");
        titleBar.setSpacing(20);
        // 左侧区域的组件
        HBox leftBox = new HBox(20);
        leftBox.setAlignment(Pos.CENTER);
        ImageView Logo=getAvatar(new ImageView(),LogoPath,20);
        leftBox.getChildren().add(Logo);
        String ChatTitle="MyChat";
        Text chatTitleLabel = new Text(ChatTitle);
        chatTitleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;-fx-fill: #014be7");
        leftBox.getChildren().add(chatTitleLabel);

        titleBar.getChildren().add(leftBox);

        // 中间区域的伸展
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleBar.getChildren().add(spacer);

        // 右侧区域的组件
        HBox rightBox = new HBox(20);
        rightBox.setAlignment(Pos.CENTER);

        // 添加通知按钮
        Button bellButton = new Button();
        bellIcon = new ImageView(new Image("file:icons/bell.png"));
        bellIcon.setFitWidth(25);
        bellIcon.setFitHeight(25);
        bellButton.setGraphic(bellIcon);
        bellButton.setStyle("-fx-background-color: transparent;");
        configureBellButton(bellButton, bellIcon);
        bellButton.setOnMouseClicked(e-> toggleSidebar());
        rightBox.getChildren().add(bellButton);

        // 添加搜索按钮
        Button searchButton = new Button();
        ImageView searchIcon = new ImageView(new Image("file:icons/search.png"));
        searchIcon.setFitWidth(20);
        searchIcon.setFitHeight(20);
        searchButton.setGraphic(searchIcon);
        searchButton.setStyle("-fx-background-color: transparent;");
        configureSearchButton(searchButton, searchIcon);
        searchButton.setOnMouseClicked(e->showSearchDialog(primaryStage));
        rightBox.getChildren().add(searchButton);

        // 显示用户头像
        ImageView avatarView = getAvatar(avatar, "", 15);


        rightBox.getChildren().add(avatarView);

        // 添加最小化按钮
        Button minButton = createMinimizeButton(primaryStage);
        rightBox.getChildren().add(minButton);

        // 添加关闭按钮
        Button closeButton = createCloseButton(primaryStage,true);
        rightBox.getChildren().add(closeButton);

        titleBar.getChildren().add(rightBox);

        // 添加鼠标拖动事件监听器
        titleBar.setOnMousePressed(LoginBasicTool::handleMousePressed);
        titleBar.setOnMouseDragged(event -> handleMouseDragged(event, primaryStage));

        return titleBar;
    }


    public static void updateSilderBar(){
        requestListView.getItems().clear();  //清除之前的记录
        requestListView.getItems().addAll(requestUsers);
        boolean flag=false;
        for(RequestRecord record: requestUsers){
            if(Objects.equals(record.getRequestStatus(), RequestStatus.PENDING.getDescription())){
                flag=true;
                break;
            }
        }
        if(flag){
            bellIcon.setImage(new Image("file:icons/redpointbell.png"));
        }
        else{
            bellIcon.setImage(new Image("file:icons/bell.png"));
        }
        isBellRedPoint=flag;

        configURequestListView(requestListView);
        System.out.println("更新请求框成功");
    }

    public static void updateFriendList(){
        friendsListView.getItems().clear();  //清除之前的记录
        friendsListView.getItems().addAll(friendsList);
        System.out.println("朋友列表更新成功");
    }


    private void ConfigEmojiTable(Stage primaryStage,TextArea inputArea)
    {
        // 创建表情框的网格布局
        GridPane emojiGrid = new GridPane();
        emojiGrid.setStyle("-fx-background-color:  #F2F2F2; -fx-border-radius: 5; -fx-background-radius: 5;");
        emojiGrid.setHgap(10);
        emojiGrid.setVgap(10);
        emojiGrid.setPadding(new Insets(10));
        emojiGrid.setPrefWidth(150);
        emojiGrid.setPrefHeight(150);

        //表情框主体
        Popup emojiPopup = new Popup();
        // 添加表情图标到网格布局中
        int row = 0, col = 0;
        for (String emojiId : emojiMap.keySet()) {
            // 创建一个按钮作为表情图标
            Button emojiButton = new Button(emojiId);
            // 创建 ImageView 并设置大小
            ImageView emojiView = new ImageView(emojiMap.get(emojiId));
            emojiView.setFitWidth(25);  // 设置图标宽度
            emojiView.setFitHeight(25); // 设置图标高度
            emojiView.setPreserveRatio(true); // 保持比例

            emojiButton.setGraphic(emojiView);
            emojiButton.setPrefSize(40, 40); // 设置按钮的大小
            emojiButton.setMinSize(40, 40); // 设置按钮的最小大小
            emojiButton.setMaxSize(40, 40); // 设置按钮的最大大小
            emojiButton.setStyle("-fx-background-color: #F2F2F2; -fx-border-color: transparent;");

            // 鼠标悬停效果
            emojiButton.setOnMouseEntered(event -> {
                emojiButton.setStyle("-fx-background-color: #bababa; ");
            });

            emojiButton.setOnMouseExited(event -> {
                emojiButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
            });

            // 点击事件处理
            emojiButton.setOnAction(event -> {
                // 获取当前输入的文本
                String currentText = inputArea.getText();
                // 将表情符号添加到输入框中
                inputArea.setText(currentText + emojiId); // 使用 emojiId 或者其他表情表示
                // 将输入框的光标移动到文本末尾
                inputArea.positionCaret(currentText.length() + emojiId.length());
                //关闭表情框
                if(emojiPopup.isShowing()){
                    emojiPopup.hide();
                }
                inputArea.requestFocus();
            });

            emojiGrid.add(emojiButton, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }

        emojiPopup.getContent().add(emojiGrid);
        emojiPopup.setAutoHide(true); // 点击外部区域时自动隐藏

            // 计算并设置 Popup 的初始位置
        updatePopupPosition(primaryStage, emojiPopup, emojiGrid);

        // 显示 Popup
        emojiPopup.show(emoji.getScene().getWindow());

        // 监听窗口移动事件以更新 Popup 位置
        emoji.getScene().getWindow().setOnShown(event ->  updatePopupPosition(primaryStage,emojiPopup,emojiGrid));
        emoji.getScene().getWindow().xProperty().addListener((observable, oldValue, newValue) ->  updatePopupPosition(primaryStage,emojiPopup,emojiGrid));
        emoji.getScene().getWindow().yProperty().addListener((observable, oldValue, newValue) ->  updatePopupPosition(primaryStage,emojiPopup,emojiGrid));
    }


    private void updatePopupPosition(Stage primaryStage,Popup emojiPopup,GridPane emojiGrid) {
        // 计算按钮的位置
        double buttonX = emoji.localToScene(0, 0).getX();
        double buttonY = emoji.localToScene(0, 0).getY();


        // 设置 Popup 的位置
        emojiPopup.setX(primaryStage.getX()+buttonX-emojiGrid.getPrefWidth()/2);
        emojiPopup.setY(primaryStage.getY()+buttonY - emojiGrid.getPrefHeight()*2.1); // 留出一些间距
    }


    private void handleFileUpload(Stage primaryStage)
    {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files","*.*"));
        File file = chooser.showOpenDialog(primaryStage);
        if (file != null) {
            Message fileMessage=new Message(nowUser,chosenUser,MessageType.FILE.getDescription(),file.getPath(),new Date());
            saveMessageListView.get(chosenUser).add(fileMessage);
            messageListView.getItems().add(fileMessage);
            //自动滚到最后一行
            messageListView.scrollTo(messageListView.getItems().size()-1);
        }
    }



    public static void main(String[] args) {
        launch(args);
    }


}
