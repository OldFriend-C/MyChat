package com.example.chatui;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.example.chatui.MQChat.GetFriendRequestClient;
import com.example.chatui.MQChat.GetMessageClient;
import com.example.chatui.MQChat.SendFriendRequestClient;
import com.example.chatui.MQChat.SendMessageClient;
import com.example.chatui.aboutUser.User;
import com.example.chatui.aboutUser.UserDeserializer;
import com.example.chatui.basic.LoginBasicTool;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.stream.Collectors;

import static com.example.chatui.basic.LoginBasicTool.*;

public class LoginApp extends Application {
    private static final String hostIP="localhost";
    public static User nowUser=new User();
    private static final String port="8888";

    private VBox vbox; // 将 vbox 设为类变量
    public static ImageView avatar=new ImageView(); // 头像显示区域
    public static File selectedFile=null;
    public static final String userUrl="http://"+hostIP+":"+port+"/user/";
    public static final String messageUrl ="http://"+hostIP+":"+port+"/message/";
    public static final String LogoPath="avatar/Logo.jpeg";
    public static final String loginurl=userUrl+"login";
    public static final String registerUserUrl=userUrl+"register/user";
    public static final String registerAvatarUrl=userUrl+"register/avatar";

    public static String nowUsername;
    public static String nowPassword;
    public static GetFriendRequestClient getFriendRequestClient;
    public static SendFriendRequestClient sendFriendRequestClient;
    public static SendMessageClient sendMessageClient;
    public static  GetMessageClient getMessageClient ;
    private PasswordField passWordDarkField=new PasswordField();
    private TextField passWordBrightFiled=new TextField();

    @Override
    public void start(Stage primaryStage) {
        HBox titleBar = LoginBasicTool.createTitleBar(primaryStage);
        vbox = new VBox();
        vbox.setPadding(new Insets(30));
        vbox.setSpacing(5);
        vbox.setStyle("-fx-background-color: #FFFFFF ;");
        vbox.setAlignment(Pos.CENTER);
        showLoginForm();

        VBox mainLayout = new VBox(titleBar, vbox);
        Scene scene = new Scene(mainLayout, 400, 400);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }




    private void showLoginForm() {
        vbox.getChildren().clear(); // 清空当前内容
        selectedFile=null;  //设置为未选中头像文件的状态
        //头像
        vbox.getChildren().add(getAvatar(avatar,LogoPath,60));
        avatar.setOnMouseClicked(null);
        selectedFile=null;


        // 用户名和密码框
        TextField userNameField = new TextField();
        userNameField.setPromptText("用户名");
        userNameField.setStyle("-fx-background-radius: 15; -fx-font-size: 14px;");
        userNameField.setMaxWidth(250);
        vbox.getChildren().add(userNameField);

        Label usernameErrorLabel = new Label();
        usernameErrorLabel.setTextFill(Color.RED);
        usernameErrorLabel.setVisible(false);
        vbox.getChildren().add(usernameErrorLabel);

        HBox passwordField=creatPassWordField();

        vbox.getChildren().add(passwordField);



        Label passwordErrorLabel = new Label();
        passwordErrorLabel.setTextFill(Color.RED);
        passwordErrorLabel.setVisible(false);
        vbox.getChildren().add(passwordErrorLabel);


        // 登录按钮
        Button loginButton = new Button("登录");
        loginButton.setMaxWidth(250);
        loginButton.setStyle("-fx-background-color: #0083ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-font-size: 16px;");
        vbox.getChildren().add(loginButton);

        // 注册文本
        Text registerText = new Text("没有账户？ 点击这里注册");
        registerText.setFont(Font.font("Arial", 12));
        registerText.setFill(Color.BLUE);
        registerText.setOnMouseClicked(event -> animateToRegisterForm());
        vbox.getChildren().add(registerText);

        // 登录按钮事件
        loginButton.setOnAction(event -> {
            String username = userNameField.getText();
            String password = passWordDarkField.getText();

            if (isValidAccountNumber(username) && isValidPassword(password)) {
                boolean result=sendLoginData(username, password);
                if(result){
                    loginAndRegisterSet(username, password);
                    ((Stage) vbox.getScene().getWindow()).close();
                }
                else{
                    showError(usernameErrorLabel,"请输入正确用户名");
                    showError(passwordErrorLabel,"请输入正确的密码");
                }
            } else {
                showError(usernameErrorLabel,"用户名是字母,数字或者下划线的组合");
                showError(passwordErrorLabel,"密码是大小写字母和数字的组合,长度在8-10之间");
            }
        });

        userNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // 焦点移出
                String userName = userNameField.getText();
                Image getavatar=getAvatarShow(userName);
                if(getavatar!=null){
                    getAvatar(avatar,getavatar,60);
                }
                else{
                    getAvatar(avatar,LogoPath,60);
                }
            }
        });

    }

    private void animateToRegisterForm() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(200), vbox);
        transition.setByX(-400); // 向左移动
        transition.setOnFinished(event ->{
            showRegisterForm();
            vbox.setTranslateX(0); // 重置位置
    });
        transition.play();
    }

    private void animateToLoginForm() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(200), vbox);
        transition.setByX(-400); // 向左移动
        transition.play();
        transition.setOnFinished(event -> {
            showLoginForm();
            vbox.setTranslateX(0); // 重置位置
        });
    }



    private void showRegisterForm() {
        vbox.getChildren().clear(); // 清空当前内容
        avatar=new ImageView();

        //上传头像可以点击
        vbox.getChildren().add(getAvatar(avatar,"avatar/add.jpg",60));
        avatar.setOnMouseClicked(e->uploadAvatar((Stage) vbox.getScene().getWindow(),false));


        // 注册用户名和密码框
        TextField userNameField = new TextField();
        userNameField.setPromptText("用户名");
        userNameField.setStyle("-fx-background-radius: 15; -fx-font-size: 14px;");
        userNameField.setMaxWidth(250);
        vbox.getChildren().add(userNameField);

        Label usernameErrorLabel = new Label();
        usernameErrorLabel.setTextFill(Color.RED);
        usernameErrorLabel.setVisible(false);
        vbox.getChildren().add(usernameErrorLabel);

        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(250);
        passwordField.setPromptText("密码");
        passwordField.setStyle("-fx-background-radius: 15; -fx-font-size: 14px;");
        vbox.getChildren().add(passwordField);

        Label passwordErrorLabel = new Label();
        passwordErrorLabel.setTextFill(Color.RED);
        passwordErrorLabel.setVisible(false);
        vbox.getChildren().add(passwordErrorLabel);

        // 注册按钮
        Button registerButton = new Button("注册");
        registerButton.setMaxWidth(250);
        registerButton.setStyle("-fx-background-color: #0083ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-font-size: 16px;");
        vbox.getChildren().add(registerButton);

        // 返回登录文本
        Text loginText = new Text("已有账户？ 点击这里登录");
        loginText.setFont(Font.font("Arial", 12));
        loginText.setFill(Color.BLUE);
        loginText.setOnMouseClicked(event -> animateToLoginForm());
        vbox.getChildren().add(loginText);

        // 注册按钮事件
        registerButton.setOnAction(event -> {
            String username = userNameField.getText();
            String password = passwordField.getText();
            if(selectedFile!=null){
                if (isValidAccountNumber(username) && isValidPassword(password)) {
                    String fileUrl=sendRegisterAvatarData(selectedFile);
                    boolean result=sendRegisterUserData(username, password,fileUrl);
                    if(result){
                        // 注册成功逻辑
                        loginAndRegisterSet(username, password);
                        ((Stage) vbox.getScene().getWindow()).close();
                    }
                    else{
                        showError(usernameErrorLabel,"该用户名已被注册");
                    }
                } else {
                    showError(usernameErrorLabel,"用户名是字母,数字或者下划线的组合");
                    showError(passwordErrorLabel,"密码是大小写字母和数字的组合,长度在8-10之间");
                }
            }
            else{
                shakeAvatar(avatar);
            }
        });


    }

    private String sendRegisterAvatarData(File selectedFile) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadAvatarDataPost = new HttpPost(registerAvatarUrl);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("avatar", selectedFile, ContentType.create("image/jpeg"), selectedFile.getName()); // 根据实际文件类型调整
            uploadAvatarDataPost.setEntity(builder.build());

            // 执行请求
            HttpResponse response = httpClient.execute(uploadAvatarDataPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode==200){
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String jsonResponse = reader.lines().collect(Collectors.joining()); // 获取响应内容
                // 解析 JSON
                JSONObject jsonObject = JSONObject.parseObject(jsonResponse);
                int code=jsonObject.getIntValue("code");
                if(code!=0){
                    return jsonObject.getString("data");
                }
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private boolean sendRegisterUserData(String username, String password,String fileUrl) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(registerUserUrl);

            // 创建 JSON 对象
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", password);
            json.put("avatar", fileUrl); // 头像的 URL
            json.put("creatAt",new Date());

            // 设置 JSON 内容
            StringEntity entity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
            post.setEntity(entity);

            // 执行请求
            HttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode==200){
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String jsonResponse = reader.lines().collect(Collectors.joining()); // 获取响应内容
                // 解析 JSON
                JSONObject jsonObject = JSONObject.parseObject(jsonResponse);
                int code=jsonObject.getIntValue("code");
                if(code==0){
                    return false;
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("发送注册数据时发生错误：" + e.getMessage());
            return false;
        }
        return false;
    }

    // 发送登录用户的信息
    private boolean sendLoginData(String username, String password) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(loginurl);

            // 创建 JSON 对象
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", password);

            // 设置 JSON 内容
            StringEntity entity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
            post.setEntity(entity);

            // 执行请求
            HttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                int code =getCode(response);
                return code==1;
            } else {
                System.err.println("登录失败，响应代码：" + statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("发送登录数据时发生错误：" + e.getMessage());
        }
        return false;
    }

    // 发送登录用户的信息
    private Image getAvatarShow(String username) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String avatarUrl=userUrl+username+"/avatar";
            HttpGet get = new HttpGet(avatarUrl);

            // 执行请求
            HttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                JSONObject jsonObject=getJsonObject(response);
                int code=jsonObject.getIntValue("code");
                //表示没有找到头像
                if(code==1){
                    String imageUrl = jsonObject.getString("data"); // 这里获取数据部分
                    return new Image(imageUrl); // 返回 Image 对象
                }
                return null;  //没找到对应用户
            } else {
                return null;  //没有找到对应用户
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; //没找到对应用户
        }
    }

    private void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> errorLabel.setVisible(false))
        );
        timeline.play();
    }

    public void loginAndRegisterSet(String username, String password) {
        nowUsername=username;
        nowPassword=password;
        nowUser=new User(nowUsername,avatar.getImage());
        //开启监听好友请求
        getFriendRequestClient =new GetFriendRequestClient(username);
        //开启准备发送好友请求的客户端
        sendFriendRequestClient=new SendFriendRequestClient();
        //开启发送好友信息的客户端
        sendMessageClient=new SendMessageClient();
        //开启监听好友信息的客户端
        getMessageClient=new GetMessageClient();

        ChatApp chatApp = new ChatApp();
        chatApp.start(new Stage());
    }

    private HBox creatPassWordField(){
        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-radius: 15;-fx-border-radius: 15;-fx-border-width: 2px;-fx-border-color: #E2E2E2;-fx-background-color: #FFFFFF;");
        hBox.setMaxWidth(250);
        StackPane passwordPane=new StackPane();
        //设置明暗文切换
        passWordDarkField.setMaxWidth(240);
        passWordDarkField.setPromptText("密码");
        passWordDarkField.setStyle("-fx-background-color: transparent;-fx-font-size: 14px");
        passWordBrightFiled = new TextField();
        passWordBrightFiled.setMaxWidth(240);
        passWordBrightFiled.setPromptText("密码");
        passWordBrightFiled.setStyle("-fx-background-color: transparent;-fx-font-size: 14px");
        passWordBrightFiled.setVisible(false);
        passWordDarkField.textProperty().addListener((observable, oldValue, newValue)->{
            passWordBrightFiled.setText(newValue);
        });
        passWordBrightFiled.textProperty().addListener((observable, oldValue, newValue)->{
            passWordDarkField.setText(newValue);
        });
        passWordDarkField.focusedProperty().addListener((observable, oldValue, newValue)->{
            if(newValue){
                hBox.setStyle("-fx-background-radius: 15;-fx-border-radius: 15;-fx-border-width: 2px;-fx-border-color: #26ACD9;-fx-background-color: #FFFFFF;");
            }
            else{
                hBox.setStyle("-fx-background-radius: 15;-fx-border-radius: 15;-fx-border-width: 2px;-fx-border-color: #E2E2E2;-fx-background-color: #FFFFFF;");
            }
        });


        passwordPane.getChildren().addAll(passWordBrightFiled,passWordDarkField);
        //设置按钮
        Button eyebutton=new Button();
        ImageView openEyeIcon = new ImageView(new Image("file:icons/closeye.png"));
        openEyeIcon.setFitWidth(20);
        openEyeIcon.setFitHeight(20);
        eyebutton.setGraphic(openEyeIcon);
        eyebutton.setStyle("-fx-background-color: transparent;");
        eyebutton.setOnAction(e -> {
            if (passWordBrightFiled.isVisible()) {
                openEyeIcon.setImage(new Image("file:icons/closeye.png"));
                passWordBrightFiled.setVisible(false);
                passWordDarkField.setText(passWordBrightFiled.getText());
                passWordDarkField.setVisible(true);
            } else {
                passWordBrightFiled.setVisible(true);
                openEyeIcon.setImage(new Image("file:icons/openeye.png"));
                passWordBrightFiled.setText(passWordDarkField.getText());
                passWordDarkField.setVisible(false);
            }
        });

        hBox.getChildren().addAll(passwordPane,eyebutton);
        return hBox;
    }



    public static void main(String[] args) {
        ParserConfig.getGlobalInstance().putDeserializer(User.class, new UserDeserializer());
        launch(args);
    }
}
