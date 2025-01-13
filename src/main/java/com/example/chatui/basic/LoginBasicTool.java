package com.example.chatui.basic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.chatui.aboutUser.User;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.chatui.LoginApp.*;

public class LoginBasicTool {
    private static double xOffset = 0;
    private static double yOffset = 0;
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
            minButton.setStyle("-fx-background-color: rgba(255,255,255,0.2)");
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
            closeButton.setStyle("-fx-background-color: rgba(255,255,255,0.2)");
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
                    // 解码 Base64 字符串为字节数组
                    byte[] imageBytes = Base64.getDecoder().decode(avatarBase64String);
                    Image avatar=new Image(new ByteArrayInputStream(imageBytes)); // 获取头像
                    String filePath="avatar/"+username+"_avatar.png";
                    saveBase64ToFile(avatarBase64String,filePath);
                    user.setAvatar(avatar);
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
        Button closeButton = createCloseButton(primaryStage);
        //完成上方搜索面板
        searchPane.getChildren().addAll(searchIcon,searchFiledAndClear,closeButton);

        //添加到整体布局中
        searchDialog.getChildren().add(searchPane);
        // 创建搜索结果列表
        // 创建场景并添加到对话框
        Scene scene = new Scene(searchDialog, 720, 500);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(LoginBasicTool.class.getResource("/com/example/chatui/styles.css").toExternalForm()); // 加载 CSS 文件
        Stage dialog = new Stage();
        // 添加鼠标拖动事件监听器
        searchDialog.setOnMousePressed(LoginBasicTool::handleMousePressed);
        searchDialog.setOnMouseDragged(event -> handleMouseDragged(event, dialog));


        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT); // 去除标题栏

        dialog.setTitle("搜索好友");
        dialog.setScene(scene);
        dialog.show();
        // 监听文本框内容变化
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // 根据文本框内容控制清除按钮的可见性
            clearButton.setVisible(!newValue.isEmpty());
        });

        // 添加回车键事件监听
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String searchusername=searchField.getText();
                List<User> friends = searchFriends(searchusername);
                showSearchResults(friends);
            }
        });


    }

    private static List<User> searchFriends(String username) {

        // 这里假设有一个方法可以根据用户名查找好友
        // 你需要实现这个方法来从服务器获取好友列表
        // 例如：
        // return getFriendsList().stream().filter(user -> user.getName().contains(username)).collect(Collectors.toList());

        // 示例返回值，实际需要根据你的逻辑来实现
        return getFriendsList().stream()
                .filter(user -> user.getName().toLowerCase().contains(username.toLowerCase()))
                .collect(Collectors.toList());
    }

    private static void showSearchResults(List<User> friends) {
        // 显示搜索结果，可以选择一种方式来展示
        // 这里使用 Alert 作为示例
        StringBuilder result = new StringBuilder("找到的好友:\n");
        for (User friend : friends) {
            result.append(friend.getName()).append("\n");
        }

        Alert alert = new Alert(AlertType.INFORMATION, result.toString(), ButtonType.OK);
        alert.setTitle("搜索结果");
        alert.showAndWait();
    }


    //TODO:存放文件测试

    public static void saveBase64ToFile(String base64String, String filepath) throws IOException {
        // 去掉 Base64 编码的头信息，如果有的话
        String cleanBase64 = base64String.replace("data:image/png;base64,", "")
                .replace("data:image/jpeg;base64,", "");

        // 解码 Base64 字符串
        byte[] bytes = Base64.getDecoder().decode(cleanBase64);

        // 保存文件
        try (FileOutputStream fos = new FileOutputStream(filepath)) {
            fos.write(bytes);
        }
    }



}
