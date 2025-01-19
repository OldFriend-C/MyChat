package com.example.chatui.basic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.chatui.aboutFriend.RequestRecord;
import com.example.chatui.aboutFriend.RequestRecordCell;
import com.example.chatui.aboutFriend.SearchFriend;
import com.example.chatui.aboutFriend.SearchFriendCell;
import com.example.chatui.aboutMessage.Message;
import com.example.chatui.aboutUser.User;
import com.example.chatui.aboutUser.UserCell;
import com.example.chatui.friendRequest.FriendRequest;
import javafx.animation.TranslateTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.chatui.ChatApp.*;
import static com.example.chatui.LoginApp.*;
import static javafx.embed.swing.SwingFXUtils.fromFXImage;
import static javax.imageio.ImageIO.write;

public class LoginBasicTool {
    private static double xOffset = 0;
    private static double yOffset = 0;

    private static List<SearchFriend> searchedFriends = new ArrayList<>();
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
    public static void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    public static void handleMouseDragged(MouseEvent event, Stage stage) {
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
    public static HBox createTitleBar(Stage primaryStage) {
        HBox titleBar = new HBox();
        titleBar.setStyle("-fx-background-color:" + toRgbString(getGradient()) + "; -fx-padding: 15;");
        titleBar.setAlignment(Pos.CENTER_RIGHT);

        Button minButton=createMinimizeButton(primaryStage);
        Button closeButton=createCloseButton(primaryStage,false);

        titleBar.getChildren().addAll(minButton, closeButton);
        // 添加鼠标拖动事件监听器
        titleBar.setOnMousePressed(LoginBasicTool::handleMousePressed);
        titleBar.setOnMouseDragged(event->handleMouseDragged(event,primaryStage));

        return titleBar;
    }



    public static void configureBellButton(Button bellButton, ImageView bellIcon) {
        bellButton.setOnMouseEntered(event -> {
            if(!isBellRedPoint){
                bellIcon.setImage(new Image("file:icons/changedbell.png"));
            }
        });
        bellButton.setOnMouseExited(event -> {
            if(!isBellRedPoint){
                bellIcon.setImage(new Image("file:icons/bell.png"));
            }
        });
    }

    public static void configureSearchButton(Button searchButton, ImageView searchIcon) {
        searchButton.setOnMouseEntered(event -> {
            searchIcon.setImage(new Image("file:icons/changedsearch.png"));
        });
        searchButton.setOnMouseExited(event -> {
            searchIcon.setImage(new Image("file:icons/search.png"));
        });
    }

    public static Button createMinimizeButton(Stage primaryStage) {
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

    public static Button createCloseButton(Stage primaryStage,boolean isCloseConnection) {
        ImageView closeIcon = new ImageView(new Image("file:icons/close-bold.png"));
        closeIcon.setFitWidth(20);
        closeIcon.setFitHeight(20);
        Button closeButton = new Button();
        closeButton.setGraphic(closeIcon);
        closeButton.setStyle("-fx-background-color: transparent;");
        closeButton.setOnAction(e -> {
            primaryStage.close();
            if(isCloseConnection){
                sendFriendRequestClient.close();
                getFriendRequestClient.close();
            }
        });
        closeButton.setOnMouseEntered(e -> {
            closeButton.setStyle("-fx-background-color: rgba(255,255,255,0.5)");
        });
        closeButton.setOnMouseExited(e -> {
            closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #564a4a;");
        });
        return closeButton;
    }

    private static final String defaultImage ="file:avatar/default.png";
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
        searchedFriends=new ArrayList<>(); //清空搜索的好友
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
        Button closeButton = createCloseButton(dialog,false);
        //完成上方搜索面板
        searchPane.getChildren().addAll(searchIcon,searchFiledAndClear,closeButton);

        //下方搜索得到的用户列表
        ListView<SearchFriend> searchedFriendsListView=new ListView<>();
        searchedFriendsListView.setCellFactory(listView -> new SearchFriendCell());
        double cellHeight=90;

        searchedFriendsListView.setPrefHeight(searchedFriends.size()*cellHeight);
        searchedFriendsListView.getItems().addAll(searchedFriends);
        searchedFriendsListView.setCellFactory(param -> new SearchFriendCell()); // 设置自定义 Cell
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
            searchedFriends=searchFriends(searchusername);
            if (searchedFriends != null) {
                showSearchResults(searchedFriends, searchedFriendsListView);
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

    private static List<SearchFriend> searchFriends(String username) {
        if(Objects.equals(username, "")){
            return new ArrayList<>();
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
                List<SearchFriend> searchFriends=new ArrayList<>();
                for(int i=0;i<dataArray.size();i++){
                    SearchFriend friend=new SearchFriend();
                    JSONObject userObj=dataArray.getJSONObject(i);
                    String avatarBase64=userObj.getString("avatar");
                    friend.setAvatar(avatarBae64ToImage(avatarBase64));
                    String status=userObj.getString("requestStatus");
                    friend.setRequestStatus(status);
                    friend.setUsername(userObj.getString("username"));
                    searchFriends.add(friend);
                }
                return searchFriends;
            }
            else {
                System.err.println("搜索好友失败,响应代码：" + statusCode);
                return null;
            }
        } catch (IOException e) {
            System.err.println("搜索好友失败:" + e.getMessage());
            return null;
        }
    }

    private static void showSearchResults(List<SearchFriend> friends,ListView<SearchFriend> listView) {
        listView.getItems().clear();
        listView.getItems().addAll(friends);
        configureSearchUserListView(listView);
    }


    public static Image avatarBae64ToImage(String avatarBase64){
        byte[] imageBytes = Base64.getDecoder().decode(avatarBase64);
        Image avatar = new Image(new ByteArrayInputStream(imageBytes));
        return avatar;
    }

    public static  void configureUserListView(ListView<User> listView) {
        listView.setCellFactory(param -> {
            UserCell cell = new UserCell();
            cell.hBox.getChildren().add(cell.leftBox);
            return cell;
        });
        //TODO: 选中事件
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            chosenUser = newValue; // 更新选中的用户
            messageList= loadMessageList(newValue);
            updataChatName();
            updateChatPane();
        });
        basicConfigListView(listView);
    }

    public static List<Message> loadMessageList(User getUser)
    {
        if(getUser==null){
            return new ArrayList<>();
        }
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(messageUrl+nowUsername+"/"+getUser.getUsername()+"/getMessages");
            // 执行请求
            HttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                JSONObject jsonObject=getJsonObject(response);
                int code=jsonObject.getIntValue("code");
                //失败
                if(code==0){
                    System.out.println("未找到相应用户");
                    return null;
                }
                JSONArray dataArray=jsonObject.getJSONArray("data");

                return dataArray.stream().map(obj -> {
                    JSONObject messageJson = (JSONObject) obj;
                    // 解析发送者和接收者
                    User senderUser = parseUser(messageJson.getJSONObject("senderUser"));
                    User receiverUser = parseUser(messageJson.getJSONObject("receiverUser"));
                    // 解析其他字段
                    String messageType = messageJson.getString("messageType");
                    String messageContent = messageJson.getString("messageContent");
                    Date createdAt = messageJson.getDate("createdAt");

                    // 创建并返回 Message 对象
                    return new Message(senderUser, receiverUser, messageType, messageContent, createdAt);
                }).collect(Collectors.toList());
            }
            else {
                System.err.println("获取好友消息记录失败,响应代码：" + statusCode);
                return null;
            }
        } catch (IOException e) {
            System.err.println("获取好友消息记录失败:" + e.getMessage());
            return null;
        }
    }

    public static void updateChatPane(){
        messageListView.getItems().clear();
        messageListView.getItems().addAll(messageList);
    }

    private static User parseUser(JSONObject userJson) {
        String username = userJson.getString("username");
        String avatarBase64 = userJson.getString("avatar");
        // 将 Base64 字符串转换为 Image 对象
        Image avatarImage=avatarBae64ToImage(avatarBase64);
        return new User(username, avatarImage);
    }


    public static  void configureSearchUserListView(ListView<SearchFriend> listView) {
        listView.setCellFactory(param -> {
            SearchFriendCell cell=new SearchFriendCell();
            cell.hBox.getChildren().addAll(cell.leftBox,cell.rightBox);
            return cell;
        });
        basicConfigListView(listView);
        //TODO:选中事件
        // 选中事件
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            chosenSearchFriend = newValue; // 更新选中的用户
//            updataChatName();
        });
    }

    public static  void configURequestListView(ListView<RequestRecord> listView) {
        listView.setCellFactory(param -> {
            RequestRecordCell cell = new RequestRecordCell();
            cell.hBox.getChildren().addAll(cell.leftBox,cell.rightBox);
            return cell;
        });
        basicConfigListView(listView);
        //TODO:选中事件
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            chosenRequestRecord = newValue; // 更新选中的用户
//            updataChatName();
        });
    }





    private static <E> void basicConfigListView(ListView<E> userListView){
        userListView.setStyle("-fx-background-color: transparent");
        userListView.setPrefHeight(800);
        // 鼠标进入事件
        userListView.setOnMouseEntered(event -> {
            userListView.lookup(".scroll-bar:vertical").setVisible(true); // 显示垂直滚动条
        });

        // 鼠标退出事件
        userListView.setOnMouseExited(event -> {
            userListView.lookup(".scroll-bar:vertical").setVisible(false); // 隐藏垂直滚动条
        });
    }



    public static List<RequestRecord> loadRequest(){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(userUrl+"/"+nowUsername+"/getRequests");
            // 执行请求
            HttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                JSONObject jsonObject=getJsonObject(response);
                int code=jsonObject.getIntValue("code");
                //失败
                if(code==0){
                    System.out.println("获取好友请求记录失败");
                    return null;
                }
                JSONArray dataArray=jsonObject.getJSONArray("data");
                List<RequestRecord> requestRecords =new ArrayList<>();
                for(int i=0;i<dataArray.size();i++){
                    RequestRecord requestRecord=getRequestFriend( dataArray.getJSONObject(i));
                    requestRecords.add(requestRecord);
                }
                return requestRecords;
            }
            else {
                System.err.println("获取好友请求记录失败,响应代码：" + statusCode);
                return null;
            }
        } catch (IOException e) {
            System.err.println("获取好友请求记录失败:" + e.getMessage());
            return null;
        }

    }

    public static RequestRecord getRequestFriend(JSONObject userObj){
        RequestRecord requestRecord=new RequestRecord();
        String avatarBase64=userObj.getString("avatar");
        requestRecord.setAvatar(avatarBae64ToImage(avatarBase64));
        String status=userObj.getString("requestStatus");
        requestRecord.setRequestStatus(status);
        requestRecord.setUsername(userObj.getString("username"));
        requestRecord.setRequestTime(userObj.getDate("requestTime"));
        return requestRecord;
    }



    public static void updataChatName() {
        Text chatname= (Text) chatPlace.getChildren().get(0);
        if(chosenUser==null){
            chatname.setText("");
        }
        else{
            chatname.setText(chosenUser.getUsername());
        }
    }


    public static void processFriendList(RequestRecord requestRecord){
        User newfriend=new User();
        newfriend.setUsername(requestRecord.getUsername());
        newfriend.setAvatar(requestRecord.getAvatar());
        boolean flag=false;
        for(User user: friendsList){
            if(Objects.equals(user.getUsername(), requestRecord.getUsername())){
                flag=true;
                break;
            }
        }
        if(flag){
            System.out.println("Friend already exists, ignore the request.");
            return;
        }
        friendsList.add(newfriend);
        updateFriendList();
    }

    public static void processFriendList(FriendRequest requestRecord,Image avatar){
        User newfriend=new User();
        newfriend.setUsername(requestRecord.getToUserUsername());
        newfriend.setAvatar(avatar);
        boolean flag=false;
        for(User user: friendsList){
            if(Objects.equals(user.getUsername(), requestRecord.getToUserUsername())){
                flag=true;
                break;
            }
        }
        if(flag){
            System.out.println("Friend already exists, ignore the request.");
            return;
        }
        friendsList.add(newfriend);
        updateFriendList();
    }



}
