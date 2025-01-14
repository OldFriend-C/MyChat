package com.example.chatui.basic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.chatui.aboutUser.User;
import com.example.chatui.aboutUser.UserCell;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import javafx.scene.control.Alert.AlertType;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.chatui.ChatApp.*;
import static com.example.chatui.LoginApp.*;

public class LoginBasicTool {
    private static double xOffset = 0;
    private static double yOffset = 0;

    private static List<User> searchFriends = new ArrayList<>();
    private LoginBasicTool() {
    }
     private static String toRgbString(Paint paint) {
        if (paint instanceof LinearGradient) {
            return "linear-gradient(to bottom , #B7DEEB,#FFFFFF)";
        }
        return "";
    }

    private static LinearGradient getGradient() {
        return new LinearGradient(
                0, 0, 1, 1,
                true,
                javafx.scene.paint.CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#1E90FF")),
                new Stop(1, Color.web("#ADD8E6"))
        );
    }
    private static void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    private static void handleMouseDragged(MouseEvent event,Stage stage) {
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
    public static HBox createTitleBar(Stage primaryStage) {
        HBox titleBar = new HBox();
        titleBar.setStyle("-fx-background-color:" + toRgbString(getGradient()) + "; -fx-padding: 15;");
        titleBar.setAlignment(Pos.CENTER_RIGHT);

        Button minButton=createMinimizeButton(primaryStage);
        Button closeButton=createCloseButton(primaryStage);

        titleBar.getChildren().addAll(minButton, closeButton);
        // 添加鼠标拖动事件监听器
        titleBar.setOnMousePressed(LoginBasicTool::handleMousePressed);
        titleBar.setOnMouseDragged(event->handleMouseDragged(event,primaryStage));

        return titleBar;
    }

    public static HBox creatChatTitle(Stage primaryStage) {
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
        ImageView bellIcon = new ImageView(new Image("file:icons/bell.png"));
        bellIcon.setFitWidth(25);
        bellIcon.setFitHeight(25);
        bellButton.setGraphic(bellIcon);
        bellButton.setStyle("-fx-background-color: transparent;");
        configureBellButton(bellButton, bellIcon);
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
        Button closeButton = createCloseButton(primaryStage);
        rightBox.getChildren().add(closeButton);

        titleBar.getChildren().add(rightBox);

        // 添加鼠标拖动事件监听器
        titleBar.setOnMousePressed(event -> handleMousePressed(event));
        titleBar.setOnMouseDragged(event -> handleMouseDragged(event, primaryStage));

        return titleBar;
    }

    private static void configureBellButton(Button bellButton, ImageView bellIcon) {
        bellButton.setOnMouseClicked(event -> {
            bellIcon.setImage(new Image("file:icons/changedbell.png"));
        });
        bellButton.setOnMouseEntered(event -> {
            bellIcon.setImage(new Image("file:icons/changedbell.png"));
        });
        bellButton.setOnMouseExited(event -> {
            bellIcon.setImage(new Image("file:icons/bell.png"));
        });
    }

    private static void configureSearchButton(Button searchButton, ImageView searchIcon) {
        searchButton.setOnMouseClicked(event -> {
            searchIcon.setImage(new Image("file:icons/changedsearch.png"));
        });
        searchButton.setOnMouseEntered(event -> {
            searchIcon.setImage(new Image("file:icons/changedsearch.png"));
        });
        searchButton.setOnMouseExited(event -> {
            searchIcon.setImage(new Image("file:icons/search.png"));
        });
    }

    private static Button createMinimizeButton(Stage primaryStage) {
        ImageView minIcon = new ImageView(new Image("file:icons/minus-bold.png"));
        minIcon.setFitWidth(20);
        minIcon.setFitHeight(20);
        Button minButton = new Button();
        minButton.setGraphic(minIcon);
        minButton.setStyle("-fx-background-color: transparent;");
        minButton.setOnAction(e -> primaryStage.setIconified(true));
        minButton.setOnMouseEntered(e -> {
            minButton.setStyle("-fx-background-color: rgba(255,255,255,0.5)");
        });
        minButton.setOnMouseExited(e -> {
            minButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #564a4a");
        });
        return minButton;
    }

    private static Button createCloseButton(Stage primaryStage) {
        ImageView closeIcon = new ImageView(new Image("file:icons/close-bold.png"));
        closeIcon.setFitWidth(20);
        closeIcon.setFitHeight(20);
        Button closeButton = new Button();
        closeButton.setGraphic(closeIcon);
        closeButton.setStyle("-fx-background-color: transparent;");
        closeButton.setOnAction(e -> primaryStage.close());
        closeButton.setOnMouseEntered(e -> {
            closeButton.setStyle("-fx-background-color: rgba(255,255,255,0.5)");
        });
        closeButton.setOnMouseExited(e -> {
            closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #564a4a;");
        });
        return closeButton;
    }

    private static String defaultImage ="file:avatar/default.png";
    public static ImageView getAvatar(ImageView avatar,String filename, int radius){
        if(avatar==null){
            avatar=new ImageView();
            avatar.setImage(new Image(defaultImage));
        }
        else{
            if(!filename.isEmpty()){
                avatar.setImage(new Image("file:"+filename));
            }
        }
        avatar.setFitWidth(radius*2); // Increased avatar size
        avatar.setFitHeight(radius*2);
        avatar.setPreserveRatio(true);
        Circle borderCircle = new Circle(radius); // Increased border size
        borderCircle.setFill(Color.TRANSPARENT);
        borderCircle.setStrokeWidth(0); // Thicker border
        Circle clipCircle = new Circle(radius); // Increased clip circle size
        clipCircle.setCenterX(radius);
        clipCircle.setCenterY(radius); // Increased clip circle size
        avatar.setClip(clipCircle);
        return avatar;
    }

    public static ImageView getAvatar(ImageView avatar,Image image, int radius){
        if(avatar==null){
            avatar=new ImageView();
            avatar.setImage(new Image(defaultImage));
        }
        else{
            if(image==null){
                avatar.setImage(new Image(defaultImage));
            }
        }
        avatar.setImage(image);
        avatar.setFitWidth(radius*2); // Increased avatar size
        avatar.setFitHeight(radius*2);
        avatar.setPreserveRatio(true);
        Circle borderCircle = new Circle(radius); // Increased border size
        borderCircle.setCenterX(radius);
        borderCircle.setCenterY(radius);
        borderCircle.setFill(Color.TRANSPARENT);
        borderCircle.setStrokeWidth(0); // Thicker border
        Circle clipCircle = new Circle(radius); // Increased clip circle size
        clipCircle.setCenterX(radius);
        clipCircle.setCenterY(radius); // Increased clip circle size
        avatar.setClip(clipCircle);
        return avatar;
    }

    public static boolean isValidAccountNumber(String accountNumber) {
        return Pattern.matches("^[a-zA-Z0-9_]{1,20}$", accountNumber);
    }

    public static boolean isValidPassword(String password) {
        String regex = "^^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]{8,10}$";
        return Pattern.matches(regex, password);
    }

    public static void shakeAvatar(ImageView avatar) {
        // 创建振动动画
        TranslateTransition shake = new TranslateTransition(Duration.millis(100), avatar);
        shake.setFromX(0);
        shake.setToX(10); // 向右移动
        shake.setCycleCount(6); // 振动次数
        shake.setAutoReverse(true); // 自动反向
        shake.play();
    }

    public static int getCode(HttpResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String jsonResponse = reader.lines().collect(Collectors.joining()); // 获取响应内容
        reader.close();
        // 解析 JSON
        JSONObject jsonObject = JSONObject.parseObject(jsonResponse);
        int code=jsonObject.getIntValue("code");
        return code;
    }

    public static JSONObject getJsonObject(HttpResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String jsonResponse = reader.lines().collect(Collectors.joining()); // 获取响应内容
        reader.close();
        // 解析 JSON
        return JSONObject.parseObject(jsonResponse);
    }


    public static void uploadAvatar(Stage primaryStage,boolean ischange) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            avatar.setImage(new Image(selectedFile.toURI().toString()));
            if(ischange){
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    HttpPost post = new HttpPost(userUrl+nowUsername+"/updateAvatar");
                    // 创建 JSON 对象
                    JSONObject json = new JSONObject();
                    // 如果需要上传头像文件，可以将其转换为 Base64 编码并加到 JSON 中
                    byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
                    String encodedString = Base64.getEncoder().encodeToString(fileContent);
                    json.put("avatar", encodedString);

                    // 设置 JSON 内容
                    StringEntity entity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
                    post.setEntity(entity);

                    // 执行请求
                    HttpResponse response = httpClient.execute(post);
                    int statusCode = response.getStatusLine().getStatusCode();

                    if (statusCode == 200) {

                    } else {
                        System.err.println("更新头像失败,响应代码：" + statusCode);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("更新头像发生错误：" + e.getMessage());
                }
            }
        }
    }

    public static List<User> getFriendsList() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(userUrl+nowUsername+"/friends");

            // 执行请求
            HttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                JSONObject jsonObject=getJsonObject(response);
                int code=jsonObject.getIntValue("code");
                //失败
                if(code==0){
                    System.out.println("获取好友列表失败");
                    return null;
                }
                JSONArray dataArray = jsonObject.getJSONArray("data");
                List<User> userList = new ArrayList<>();
                // 遍历 JSONArray，创建 User 对象并添加到 List
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONObject userObject = dataArray.getJSONObject(i);
                    String username = userObject.getString("username");
                    User user = new User();
                    user.setUsername(username);
                    String avatarBase64String = userObject.getString("avatar"); // 这里获取数据部分
                    user.setAvatar(avatarBae64ToImage(avatarBase64String));
                    userList.add(user);
                }
                return userList;

            } else {
                System.err.println("获取好友列表失败,响应代码：" + statusCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("获取好友列表发生错误：" + e.getMessage());
            return null;
        }
    }

    public static void showSearchDialog(Stage primaryStage) {
        // 创建搜索框
        searchFriends=new ArrayList<>(); //清空搜索的好友
        Stage dialog = new Stage();
        HBox searchPane=new HBox(10);
        searchPane.setMaxWidth(700);
        searchPane.setAlignment(Pos.CENTER);
        searchPane.setPadding(new Insets(5, 5, 10, 10));
        VBox searchDialog=new VBox();
        // 为搜索框设置 CSS 样式
        searchDialog.getStyleClass().add("search-dialog");
        // 添加搜索图标
        ImageView searchIcon = new ImageView(new Image("file:icons/friendSearch.png"));
        searchIcon.setFitWidth(20);
        searchIcon.setFitHeight(20);

        HBox searchFiledAndClear =new HBox(10);

        //添加输入框
        TextField searchField = new TextField();
        searchField.setStyle("-fx-font-size: 14px;-fx-border-color: transparent;-fx-background-color: transparent");
        searchField.setPrefWidth(600);
        searchField.setFocusTraversable(false);
        searchField.setPromptText("输入用户名搜索好友,按回车搜索...");

        //添加清空按钮
        Button clearButton = new Button();
        ImageView clearIcon = new ImageView(new Image("file:icons/clear.png"));
        clearIcon.setFitWidth(20);
        clearIcon.setFitHeight(20);
        clearButton.setGraphic(clearIcon);
        clearButton.setStyle("-fx-background-color: transparent");
        clearButton.setVisible(false);  //初始不可见
        clearButton.setOnAction(event -> searchField.clear());

        searchFiledAndClear.setMaxWidth(650);
        searchFiledAndClear.getStyleClass().add("search-pane");
        searchFiledAndClear.getChildren().addAll(searchField,clearButton);

        //添加关闭按钮
        Button closeButton = createCloseButton(dialog);
        //完成上方搜索面板
        searchPane.getChildren().addAll(searchIcon,searchFiledAndClear,closeButton);

        //下方搜索得到的用户列表
        ListView<User> searchedFriendsListView=new ListView<>();
        searchedFriendsListView.setCellFactory(listView -> new UserCell());
        double cellHeight=90;

        searchedFriendsListView.setPrefHeight(searchFriends.size()*cellHeight);
        searchedFriendsListView.getItems().addAll(searchFriends);
        searchedFriendsListView.setCellFactory(param -> new UserCell()); // 设置自定义 Cell
        searchedFriendsListView.setStyle("-fx-background-color: transparent");

        //添加到整体布局中
        searchDialog.getChildren().addAll(searchPane,searchedFriendsListView);
        // 创建搜索结果列表

        Scene scene = new Scene(searchDialog, 720, 500);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(LoginBasicTool.class.getResource("/com/example/chatui/styles.css").toExternalForm()); // 加载 CSS 文件

        // 添加鼠标拖动事件监听器
        searchDialog.setOnMousePressed(LoginBasicTool::handleMousePressed);
        searchDialog.setOnMouseDragged(event -> handleMouseDragged(event, dialog));


        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT); // 去除标题栏

        dialog.setScene(scene);
        dialog.show();
        // 监听文本框内容变化
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // 根据文本框内容控制清除按钮的可见性
            clearButton.setVisible(!newValue.isEmpty());
            String searchusername=searchField.getText();
            List<User> friends = searchFriends(searchusername);
            if (friends != null) {
                showSearchResults(friends, searchedFriendsListView);
            }
        });

        // 鼠标进入事件
        searchDialog.setOnMouseEntered(event -> {
            searchedFriendsListView.lookup(".scroll-bar:vertical").setVisible(true); // 显示垂直滚动条
        });

        // 鼠标退出事件
        searchDialog.setOnMouseExited(event -> {
            searchedFriendsListView.lookup(".scroll-bar:vertical").setVisible(false); // 显示垂直滚动条
        });


    }

    //TODO:搜索好友功能
    private static List<User> searchFriends(String username) {
        if(Objects.equals(username, "")){
            return new ArrayList<User>();
        }
        // 示例返回值，实际需要根据你的逻辑来实现
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(userUrl+username+"/"+nowUsername+"/searchFriends");
            // 执行请求
            HttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {


                JSONObject jsonObject=getJsonObject(response);
                int code=jsonObject.getIntValue("code");
                //失败
                if(code==0){
                    System.out.println("搜索好友失败");
                    return null;
                }
                JSONArray dataArray=jsonObject.getJSONArray("data");
                List<User> searchFriends=new ArrayList<>();
                for(int i=0;i<dataArray.size();i++){
                    User friend=new User();
                    JSONObject userObj=dataArray.getJSONObject(i);
                    String friendname=userObj.getString("username");
                    friend.setUsername(friendname);
                    String avatarBase64=userObj.getString("avatar");
                    friend.setAvatar(avatarBae64ToImage(avatarBase64));
                    searchFriends.add(friend);
                }
                return searchFriends;
            }
            else {
                System.err.println("搜索好友失败,响应代码：" + statusCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("搜索好友失败:" + e.getMessage());
            return null;
        }
    }

    private static void showSearchResults(List<User> friends,ListView<User> listView) {
        listView.getItems().clear();
        listView.setStyle("-fx-background-color: transparent");
        listView.getItems().addAll(friends);
        configureUserListView(listView,true);

    }


    private static Image avatarBae64ToImage(String avatarBase64){
        byte[] imageBytes = Base64.getDecoder().decode(avatarBase64);
        Image avatar = new Image(new ByteArrayInputStream(imageBytes));
        return avatar;
    }

    public static void configureUserListView(ListView<User> listView, boolean showAddFriendButton) {
        double cellHeight = 90;
        listView.setPrefHeight(listView.getItems().size() * cellHeight);
        listView.setCellFactory(param -> {
            UserCell cell = new UserCell();
            if (showAddFriendButton) {
                cell.leftBox.setPrefWidth(200);
                cell.rightBox.setPrefWidth(450);
                cell.addFriendButton.setFocusTraversable(false);
                cell.addFriendButton.getStyleClass().add("add-friend-button");
                cell.rightBox.setAlignment(Pos.CENTER_RIGHT);
                cell.rightBox.getChildren().add(cell.addFriendButton);
                cell.hBox.getChildren().addAll(cell.leftBox, cell.rightBox);
            }
            else{
                cell.hBox.getChildren().add(cell.leftBox);
            }
            return cell;
        });
        listView.setStyle("-fx-background-color: transparent");

        // 鼠标进入事件
        listView.setOnMouseEntered(event -> {
            listView.lookup(".scroll-bar:vertical").setVisible(true); // 显示垂直滚动条
        });

        // 鼠标退出事件
        listView.setOnMouseExited(event -> {
            listView.lookup(".scroll-bar:vertical").setVisible(false); // 隐藏垂直滚动条
        });
        // 选中事件
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            chosenUser = newValue; // 更新选中的用户
            updataChatName();
        });

    }


    public static void updataChatName() {
        Text chatname= (Text) chatPlace.getChildren().get(0);
        if(chosenUser==null){
            chatname.setText("");
        }
        else{
            chatname.setText(chosenUser.getName());
        }
    }

    public static boolean isFriendWithCurrentUser(User user){
        for(User friend: friendsList){
            if(user.getName().equals(friend.getName())){
                return true;
            }
        }
        return false;
    }



}
