package com.example.chatui.aboutMessage;

import com.example.chatui.basic.FileClient;
import com.example.chatui.basic.OSSUtils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.example.chatui.ChatApp.chosenUser;
import static com.example.chatui.LoginApp.nowUser;
import static com.example.chatui.LoginApp.nowUsername;
import static com.example.chatui.basic.LoginBasicTool.parseMessage;

public class MessageCell extends ListCell<Message> {
    private final HBox hBox = new HBox(10);
    private final VBox vBox = new VBox(5);
    private final VBox info=new VBox(5);
    private final Circle avatarCircle = new Circle(30);
    private final ImageView avatarImageView = new ImageView();
    private final Text sendernameText = new Text();
    private final TextFlow messageContainer=new TextFlow ();
    private final VBox fileContainer=new VBox();


    @Override
    protected void updateItem(Message chatMessage, boolean empty) {
        super.updateItem(chatMessage, empty);

        // 清除之前的内容
        hBox.getChildren().clear();
        vBox.getChildren().clear();
        //设置头像
        avatarCircle.setCenterX(30);
        avatarCircle.setCenterY(30);
        avatarImageView.setFitWidth(60);
        avatarImageView.setFitHeight(60);
        avatarImageView.setClip(avatarCircle);
        info.getChildren().clear();
        if (empty || chatMessage == null) {
            avatarImageView.setImage(null);
            sendernameText.setText("");
            setGraphic(null);
        } else {
            setStyle("-fx-background-color: transparent;");
            try{
                avatarImageView.setImage(chatMessage.getSenderUser().getAvatar());
            } catch (Exception e) {
                System.out.println("发送者头像加载错误");
                e.printStackTrace();
            }
            hBox.setStyle("-fx-background-color: transparent;" + "-fx-padding: 10px;");
            sendernameText.setText(chatMessage.getSenderUser().getUsername());
            sendernameText.setStyle("-fx-font-weight: bold;");



            //处理表情以及文字混合的消息
            if(Objects.equals(chatMessage.getMessageType(), MessageType.TEXT.getDescription())) {
                //发送者信息组件
                info.getChildren().addAll(sendernameText,messageContainer);

                processTextContent(chatMessage);

                if(chatMessage.getSenderUser().getUsername().equals(nowUsername)){
                    info.setAlignment(Pos.CENTER_RIGHT);
                    messageContainer.setStyle("-fx-background-color: #0099ff; " +
                            "-fx-padding: 10px; " +
                            "-fx-background-radius: 15px;" +
                            "-fx-border-color: transparent;");
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.getChildren().addAll(info, avatarImageView);
                }
                else{
                    info.setAlignment(Pos.CENTER_LEFT);
                    messageContainer.setStyle("-fx-background-color: #F2F2F2; " +
                            "-fx-padding: 10px; " +
                            "-fx-background-radius: 15px;" +
                            "-fx-border-color: transparent;");
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.getChildren().addAll(avatarImageView, info);
                }
            }
            else if(Objects.equals(chatMessage.getMessageType(), MessageType.FILE.getDescription())){  //处理文件消息
                info.getChildren().addAll(sendernameText,fileContainer);
                processFileContent(chatMessage);

                //自己发
                if(chatMessage.getSenderUser().getUsername().equals(nowUsername)){
                    info.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setAlignment(Pos.TOP_RIGHT);
                    hBox.getChildren().addAll(info, avatarImageView);
                }
                else{ //对方发
                    info.setAlignment(Pos.CENTER_LEFT);
                    hBox.setAlignment(Pos.TOP_LEFT);
                    hBox.getChildren().addAll(avatarImageView, info);
                }

            }
            vBox.getChildren().add(hBox);
            setGraphic(vBox);

        }
    }

    private void processTextContent(Message chatMessage)
    {
        String sendmsg=chatMessage.getMessageContent();
        List<ContentElemNode> msgContent=parseMessage(sendmsg);
        messageContainer.setMaxWidth(600);
        Platform.runLater(() -> {
            messageContainer.getChildren().clear();
            for (ContentElemNode contentElemNode : msgContent) {
                messageContainer.getChildren().add(contentElemNode.toUi());
            }
        });

    }

    private void processFileContent(Message chatMessage){

        VBox tempFileContainer=new VBox();
        FileClient fileClient=new FileClient();
        String senderUsername=chatMessage.getSenderUser().getUsername();
        //表示对方或者自己在之前已经上传了文件
        if(chatMessage.getIsUploaded() && (Objects.equals(senderUsername, chosenUser.getUsername()) || Objects.equals(senderUsername, nowUser.getUsername()))){
            String objectName=chatMessage.getMessageContent();
            FileInfo fileInfo=OSSUtils.getFileInfo(objectName);
            String fileName=fileInfo.getFileName();
            long fileSize=fileInfo.getFileLength();
            tempFileContainer=fileClient.showDownloadedUI(objectName,fileName,fileSize);

        }
        else if(!chatMessage.getIsUploaded() && Objects.equals(senderUsername, nowUsername)){  //当前用户正在上传文件
            // 创建临时容器避免成员变量污染
            File file=new File(chatMessage.getMessageContent());
            tempFileContainer = fileClient.createUploadUI(file, chatMessage);

            chatMessage.setIsUploaded(true);
        }
        //对方正在上传文件


        // 确保UI更新在主线程
        VBox finalNewFileContainer = tempFileContainer;
        Platform.runLater(() -> {

            fileContainer.getChildren().clear();
            fileContainer.getChildren().addAll(finalNewFileContainer.getChildren());
            fileContainer.setStyle("-fx-background-color: #F2F2F2; -fx-padding: 10px; -fx-background-radius: 15px;-fx-border-color: transparent;");

            // 强制触发布局更新
            fileContainer.requestLayout();
            hBox.requestLayout();
        });

    }



}