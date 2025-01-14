package com.example.chatui;

import com.alibaba.fastjson.JSONObject;
import com.example.chatui.MQChat.ChatClient;
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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.stream.Collectors;

import static com.example.chatui.basic.LoginBasicTool.*;

public class LoginApp extends Application {
    private static final String hostIP="192.168.1.108";
    private static final String port="8888";

    private VBox vbox; // 将 vbox 设为类变量
    public static ImageView avatar=new ImageView(); // 头像显示区域
    public static File selectedFile=null;
    public static final String userUrl="http://"+hostIP+":"+port+"/user/";
    public static final String LogoPath="avatar/Logo.jpeg";
    public static final String loginurl=userUrl+"login";
    public static final String registerUrl=userUrl+"register";

    public static String nowUsername;
    public static String nowPassword;
    public static ChatClient chatClient;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("登录界面");
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


        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(250);
        passwordField.setPromptText("密码");
        passwordField.setStyle("-fx-background-radius: 15; -fx-font-size: 14px;");
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
            String password = passwordField.getText();

            if (isValidAccountNumber(username) && isValidPassword(password)) {
                boolean result=sendLoginData(username, password);
                if(result){
                    nowUsername=username;
                    nowPassword=password;
                    chatClient=new ChatClient(username);
                    ChatApp chatApp = new ChatApp();
                    chatApp.start(new Stage());
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
                    boolean result=sendRegisterData(username, password);
                    if(result){
                        // 注册成功逻辑
                        nowUsername=username;
                        nowPassword=password;
                        ChatApp chatApp = new ChatApp();
                        chatApp.start(new Stage());
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

    private boolean sendRegisterData(String username, String password) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(registerUrl);

            // 创建 JSON 对象
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", password);

            // 如果需要上传头像文件，可以将其转换为 Base64 编码并加到 JSON 中
            if (avatar.getImage() != null) {
                // 获取 ImageView 中的 Image 对象
                Image image = avatar.getImage();
                byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                json.put("avatar", encodedString);
            }

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

            // 如果需要上传头像文件，可以将其转换为 Base64 编码并加到 JSON 中
            if (selectedFile != null) {
                byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                json.put("avatar", encodedString);
            }

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
                    String base64String = jsonObject.getString("data"); // 这里获取数据部分
                    // 解码 Base64 字符串为字节数组
                    byte[] imageBytes = Base64.getDecoder().decode(base64String);
                    return new Image(new ByteArrayInputStream(imageBytes)); // 返回 Image 对象
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

    public static void main(String[] args) {
        launch(args);
    }
}
