package com.example.chatui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class test extends Application {
    private VBox messageContainer = new VBox();
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    public void start(Stage primaryStage) {
        // 主界面布局
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        // 消息区域
        ScrollPane scrollPane = new ScrollPane(messageContainer);
        scrollPane.setFitToWidth(true);
        messageContainer.setSpacing(10);
        messageContainer.setPadding(new Insets(10));

        // 底部控制栏
        HBox bottomBar = new HBox(10);
        Button uploadBtn = new Button("上传文件");
        uploadBtn.setOnAction(e -> handleFileUpload(primaryStage));

        bottomBar.setPadding(new Insets(10));
        bottomBar.getChildren().add(uploadBtn);

        root.setCenter(scrollPane);
        root.setBottom(bottomBar);

        primaryStage.setTitle("QQ风格文件上传演示");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleFileUpload(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files","*.*"));
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            createUploadItem(file);
        }
    }

    private void createUploadItem(File file) {
        HBox uploadInfo = new HBox(10);
        uploadInfo.getStyleClass().add("upload-item");
        uploadInfo.setPadding(new Insets(8));

        // 文件图标
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("file-icon.png")));
        icon.setFitWidth(32);
        icon.setFitHeight(32);

        // 文件信息
        VBox fileInfo = new VBox(5);
        fileInfo.getChildren().addAll(
                new Label(file.getName()),
                new Label(formatSize(file.length()))

        );

        // 进度组件
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(250);
        progressBar.getStyleClass().add("modern-progress");
        Button cancelBtn = createCancelButton();

        HBox progressRow = new HBox(10, progressBar, cancelBtn);
        VBox progressArea = new VBox(5, progressRow);

        VBox updataFileMessage=new VBox();
        uploadInfo.getChildren().addAll(fileInfo,icon);
        updataFileMessage.getChildren().addAll(uploadInfo,progressArea);

        messageContainer.getChildren().add(updataFileMessage);

        // 启动上传任务
        startUploadService(file, progressBar, cancelBtn);
    }

    private Button createCancelButton() {
        Button btn = new Button("×");
        btn.getStyleClass().add("cancel-btn");
        return btn;
    }

    private void startUploadService(File file, ProgressBar bar, Button cancelBtn) {
        Service<Void> service = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new UploadTask(file);
            }
        };

        bar.progressProperty().bind(service.progressProperty());

        cancelBtn.setOnAction(e -> {
            service.cancel();
            bar.progressProperty().unbind();
        });

        service.setOnSucceeded(e -> Platform.runLater(() -> {
            bar.setProgress(1);
        }));

        threadPool.submit(service::start);
    }

    class UploadTask extends Task<Void> {
        final File file;

        UploadTask(File file) {
            this.file = file;
        }

        @Override
        protected Void call() throws Exception {
            try (FileInputStream fis = new FileInputStream(file)) {
                long total = file.length();
                long uploaded = 0;
                byte[] buffer = new byte[4096];

                while (uploaded < total && !isCancelled()) {
                    int read = fis.read(buffer);
                    // 此处替换为实际网络传输代码
                    Thread.sleep(50);

                    uploaded += read;
                    updateProgress(uploaded, total);
                    updateMessage(String.format("%.1f%% - %.1f KB/s",
                            (uploaded*100.0/total),
                            (read/0.05/1024)));
                }
            }
            return null;
        }
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        String[] units = {"KB", "MB", "GB"};
        int unitIndex = (int) (Math.log(bytes) / Math.log(1024)) - 1;
        return String.format("%.1f %s", bytes / Math.pow(1024, unitIndex + 1), units[unitIndex]);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
