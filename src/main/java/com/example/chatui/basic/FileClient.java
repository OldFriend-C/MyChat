package com.example.chatui.basic;

import com.alibaba.fastjson.JSONObject;
import com.example.chatui.aboutMessage.Message;
import com.example.chatui.aboutMessage.MessageType;
import com.example.chatui.aboutMessage.SendMsg;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.example.chatui.ChatApp.chosenUser;
import static com.example.chatui.LoginApp.*;
import static com.example.chatui.basic.LoginBasicTool.getJsonObject;
import static com.example.chatui.basic.LoginBasicTool.getSuffix;

public class FileClient {
    private static final int CHUNK_SIZE = 5 * 1024 * 1024; // 5MB分片
    private static final PoolingHttpClientConnectionManager CONN_MANAGER = new PoolingHttpClientConnectionManager();

    // 静态初始化块配置连接池（线程安全）
    static {
        // 连接池参数配置（根据服务器承载能力调整）
        CONN_MANAGER.setMaxTotal(200);         // 最大连接数
        CONN_MANAGER.setDefaultMaxPerRoute(50);// 每个路由最大连接数
        CONN_MANAGER.setValidateAfterInactivity(1000); // 空闲连接验证间隔(ms)
    }

    private static final ExecutorService UPLOAD_EXECUTOR =
            Executors.newFixedThreadPool(4, r -> {
                Thread t = new Thread(r);
                t.setDaemon(true); // 设为守护线程（防止应用无法退出）
                return t;
            });

    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.custom()
            .setConnectionManager(CONN_MANAGER)
            .setConnectionManagerShared(true)  // 关键：允许多个线程共享连接池
            .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectTimeout(15000)      // 保留原有超时配置
                    .setSocketTimeout(30000)
                    .setExpectContinueEnabled(true)
                    .build())
            .evictExpiredConnections()         // 自动清理过期连接
            .evictIdleConnections(30, TimeUnit.SECONDS) // 30秒空闲超时
            .build();

    public VBox createUploadUI(File file, Message chatMessage) {
        VBox uploadItem = new VBox(10);
        uploadItem.getStyleClass().add("upload-item");
        String extention=getSuffix(file.getName());
        String fileIconPath="fileIcons/"+extention+".png";
        // 文件信息展示
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(fileIconPath)));
        icon.setFitHeight(45);
        icon.setFitWidth(45);

        Label fileNameLabel = new Label(shortenFileName(file.getName(),20,3));
        fileNameLabel.setWrapText(true);
        fileNameLabel.setPrefWidth(200);
        fileNameLabel.setMaxWidth(200);
        fileNameLabel.setStyle("-fx-font-size: 15px;"); // 设置字体大小为20px

        Label sizeLabel = new Label(formatSize(file.length()));
        sizeLabel.setStyle("-fx-font-size:13px;");

        // 进度组件
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        Label speedLabel = new Label();
        Button cancelBtn = createCancelButton();

        // 布局组装
        VBox fileInfo = new VBox(5, fileNameLabel, sizeLabel);
        HBox progressRow = new HBox(10, progressBar, cancelBtn);
        progressRow.setAlignment(Pos.CENTER);
        HBox container = new HBox(10, fileInfo,icon);
        uploadItem.getChildren().addAll(container, progressRow);

//         启动上传任务
        UploadTask task = new UploadTask(file, chatMessage,cancelBtn);
        progressBar.progressProperty().bind(task.progressProperty());
        speedLabel.textProperty().bind(task.messageProperty());

        cancelBtn.setOnAction(e -> {
            task.cancel(true);
            progressBar.progressProperty().unbind();
        });

        UPLOAD_EXECUTOR.submit(task);
        return uploadItem;
    }

    private class UploadTask extends Task<Void> {
        private final File file;

        private Message chatMessage;
        private boolean isUploadComplete = false;
        private Button cancelBtn; // 新增取消按钮引用


//        private String BOUNDARY =  "DS_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8); // 自定义边界

        UploadTask(File file, Message chatMessage,Button cancelBtn) {
            this.file = file;
            this.chatMessage=chatMessage;
            this.cancelBtn=cancelBtn;
        }

        @Override
        protected Void call() {
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {

                final long fileSize = raf.length();
                final int totalChunks = (int) Math.ceil(fileSize / (double) CHUNK_SIZE);

                for (int chunkNum = 0; chunkNum < totalChunks; chunkNum++) {
                    if (isCancelled()) break;

                    // 读取分片数据
                    raf.seek((long) chunkNum * CHUNK_SIZE);
                    byte[] buffer = new byte[CHUNK_SIZE];
                    int bytesRead = raf.read(buffer);

                    // 构建请求
                    HttpPost httpPost = new HttpPost(buildUploadUrl());
                    // 添加自定义请求头
                    httpPost.setHeader("X-Client-ID", nowUsername + System.currentTimeMillis());
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                            .addPart("file", new ByteArrayBody(
                                    Arrays.copyOf(buffer, bytesRead),
                                    file.getName()))
                            .addTextBody("chunkNum", String.valueOf(chunkNum))
                            .addTextBody("totalChunks", String.valueOf(totalChunks));

                    httpPost.setEntity(builder.build());

                    // 执行上传
                    try (CloseableHttpResponse response = HTTP_CLIENT.execute(httpPost)) {
                        boolean isFinished=chunkNum==totalChunks-1;
                        validateResponse(response,isFinished);
                        updateProgress(chunkNum + 1, totalChunks);
                    }
                }
                isUploadComplete = true; // 上传完成后设置标志位
                Platform.runLater(this::updateCancelButtonIcon); // 更新取消按钮图标
            }catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return null;
        }


        private void updateCancelButtonIcon() {
            if (isUploadComplete && cancelBtn != null) {
                updateButtonState(cancelBtn,file.getName());
            }
        }
        private String buildUploadUrl() {
            return messageUrl+nowUsername+"/"+chosenUser.getUsername()+"/sendFile";
        }

        private void validateResponse(CloseableHttpResponse response,boolean isUploadFinshed) throws IOException {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.out.println("连接服务端失败");
                throw new IOException("Upload failed: " +
                        response.getStatusLine().getReasonPhrase());
            }
            else {
                JSONObject jsonObject=getJsonObject(response);
                int code=jsonObject.getIntValue("code");
                if(code==1){
                    System.out.println("分片上传成功");
                }
                else {
                    System.out.println("分片上传失败");
                }
                if(isUploadFinshed){  //在传输文件成功后要设置文件内容
                    String objectName=jsonObject.getString("data");
                    chatMessage.setMessageContent(objectName);
                    System.out.println("文件名设置完成");
                    //发送文件消息
                    Platform.runLater(() -> {
                        SendMsg sendmsg=new SendMsg(nowUsername,chosenUser.getUsername(), MessageType.FILE.getDescription(),objectName,new Date());
                        sendMessageClient.sendMessage(sendmsg);
                        System.out.println("文件消息已发送");
                    });
                }
            }
        }

    }


    // 在MessageCell类中添加
    public VBox showDownloadedUI(String objectName,String fileName,long fileSize) {
        // 容器初始化
        VBox downloadContainer= new VBox(10);
        downloadContainer.getStyleClass().add("upload-item");
        String extention=getSuffix(fileName);
        String fileIconPath="fileIcons/"+extention+".png";
        // 文件信息展示
        ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(fileIconPath))));
        icon.setFitHeight(45);
        icon.setFitWidth(45);

        Label fileNameLabel = new Label(shortenFileName(fileName,20,3));
        fileNameLabel.setWrapText(true);
        fileNameLabel.setPrefWidth(200);
        fileNameLabel.setMaxWidth(200);
        fileNameLabel.setStyle("-fx-font-size: 15px;"); // 设置字体大小为20px

        Label sizeLabel = new Label(formatSize(fileSize));
        sizeLabel.setStyle("-fx-font-size:13px;");

        // 下载按钮
        Button downloadBtn = createDownloadButton(objectName,fileName);
        downloadBtn.getStyleClass().add("download-btn");
        HBox downliadBtnContainer=new HBox(downloadBtn);
        downliadBtnContainer.setAlignment(Pos.TOP_RIGHT);

        // 布局组装
        VBox fileInfo = new VBox(5, fileNameLabel, sizeLabel);
        HBox container=new HBox(10,fileInfo,icon);
        downloadContainer.getChildren().addAll(container, downliadBtnContainer);

        downloadBtn.setOnAction(e -> {
            //TODO: 调用下载逻辑


        });


        return downloadContainer;
    }


    // 智能按钮生成
    private Button createDownloadButton(String ossUrl, String fileName) {
        Button btn = new Button();
        btn.setPrefWidth(80);
        // 初始状态检测
        updateButtonState(btn, fileName);
        // 点击事件绑定
        btn.setOnAction(e -> {
            if (!isFileExists(fileName)) {
                startDownload(ossUrl, fileName, btn);
            }
        });

        return btn;
    }

    // 按钮状态更新
    private void updateButtonState(Button btn, String fileName) {
        btn.setGraphic(null);
        btn.setPrefWidth(10);
        btn.setPrefHeight(10);
        if (isFileExists(fileName)) {
            String finishIconPath="file:icons/check.png";
            ImageView finishIcon=new ImageView(new Image(finishIconPath));
            finishIcon.setFitWidth(20);
            finishIcon.setFitHeight(20);
            btn.setGraphic(finishIcon);
            btn.setStyle("-fx-background-color: transparent;");
            btn.setDisable(true);
        } else {
            String downloadIconPath="file:icons/download.png";
            ImageView downloadIcon=new ImageView(new Image(downloadIconPath));
            downloadIcon.setFitHeight(20);
            downloadIcon.setFitWidth(20);
            btn.setGraphic(downloadIcon);
            btn.getStyleClass().add("download-button");
            btn.setDisable(false);
        }
    }

    // 下载逻辑（含状态更新）
    private void startDownload(String url, String fileName, Button btn) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        btn.setDisable(true);
        String cancelIconPath="file:icons/cancel.png";
        ImageView cancelIcon=new ImageView(new Image(cancelIconPath));
        cancelIcon.setFitHeight(20);
        cancelIcon.setFitWidth(20);
        btn.setGraphic(cancelIcon);
        btn.setDisable(false);

        executor.execute(() -> {
            try (InputStream in = new URL(url).openStream(); // OSS URL 已创建
                 OutputStream out = new FileOutputStream(getDownloadPath(fileName))) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                // 更新UI
                Platform.runLater(() -> {
                    updateButtonState(btn, fileName);
                });

            } catch (IOException e) {
                Platform.runLater(() -> {
                    btn.setText("下载失败");
                    btn.setStyle("-fx-text-fill: red;");
                });
            } finally {
                // 确保按钮状态正确
                Platform.runLater(() -> btn.setDisable(false));
            }
        });
    }




    private Button createCancelButton() {
        Button btn=new Button();
        String cancelIconPath="file:icons/cancel.png";
        ImageView cancelIcon=new ImageView(new Image(cancelIconPath));
        cancelIcon.setFitWidth(20);
        cancelIcon.setFitHeight(20);
        btn.setGraphic(cancelIcon);
        btn.getStyleClass().addAll("cancel-btn");
        return btn;
    }

    private String formatSize(long bytes) {
        // 实现文件大小格式化逻辑
        return String.format("%.2f MB", bytes / 1024.0 / 1024);
    }

    public static String shortenFileName(String filePath, int prefixLength, int suffixLength) {
        File file = new File(filePath);

        String fileName = file.getName();
        // 获取文件扩展名
        String extension = "";
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex >= 0) {
            extension = fileName.substring(dotIndex); // 获取扩展名
            fileName = fileName.substring(0, dotIndex); // 去掉扩展名部分
        }

        // 处理文件名
        if (fileName.length() <= prefixLength + suffixLength) {
            return fileName + extension; // 如果文件名较短，直接返回
        }

        // 获取开头和结尾部分
        String prefix = fileName.substring(0, Math.min(prefixLength, fileName.length()));
        String suffix = fileName.substring(Math.max(fileName.length() - suffixLength, prefixLength)
        );

        return prefix + ". . ." + suffix + extension; // 返回处理后的文件名
    }


    //检查文件是否存在本地
    private boolean isFileExists(String fileName) {
        return Files.exists(Paths.get(getDownloadPath(fileName)));
    }

    // 获取下载路径（同前）
    public String getDownloadPath(String fileName) {
        return Paths.get(System.getProperty("user.home"), "Downloads", fileName).toString();
    }
}
