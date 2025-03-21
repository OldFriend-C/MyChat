package com.example.chatui;// FileUploadHandler.java
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Setter
public class FileUploadHandler {
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    private VBox messageContainer; // 消息容器需在界面初始化时注入



    // 入口方法
    public void initiateFileUpload(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            createUploadMessageItem(selectedFile);
        }
    }

    // 创建上传消息条目
    private void createUploadMessageItem(File file) {
        HBox uploadItem = new HBox(10);
        uploadItem.getStyleClass().add("upload-item");
        uploadItem.setPadding(new Insets(8));

        // 文件信息区域
        ImageView icon = new ImageView(new Image("icons/file.png"));
        icon.setFitWidth(32);
        icon.setFitHeight(32);

        VBox fileInfo = new VBox(5,
                new Label(file.getName()),
                new Label(formatFileSize(file.length()))
        );

        // 进度控制区域
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        Label statusLabel = new Label("等待上传...");
        Button cancelBtn = createCancelButton();

        VBox progressArea = new VBox(5,
                new HBox(10, progressBar, cancelBtn),
                statusLabel
        );

        uploadItem.getChildren().addAll(icon, fileInfo, progressArea);
        messageContainer.getChildren().add(0, uploadItem);

        // 启动上传任务
        startUploadService(file, progressBar, statusLabel, cancelBtn);
    }

    // 创建取消按钮
    private Button createCancelButton() {
        Button btn = new Button("×");
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff4444;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #eeeeee;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent;"));
        return btn;
    }

    // 启动上传服务
    private void startUploadService(File file, ProgressBar progressBar,
                                    Label statusLabel, Button cancelBtn) {
        Service<Void> service = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new UploadTask(file);
            }
        };

        // 绑定UI属性
        progressBar.progressProperty().bind(service.progressProperty());
        statusLabel.textProperty().bind(service.messageProperty());

        // 取消操作
        cancelBtn.setOnAction(e -> {
            service.cancel();
            statusLabel.setText("已取消");
            progressBar.progressProperty().unbind();
        });



        threadPool.submit(service::start);
    }

    // 上传任务类
    private static class UploadTask extends Task<Void> {
        private final File file;

        public UploadTask(File file) {
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
                    // 模拟网络传输（替换为实际传输代码）
                    Thread.sleep(50);

                    uploaded += read;
                    updateProgress(uploaded, total);
                    updateMessage(String.format("%.1f%% - %.1f KB/s",
                            (uploaded*100.0/total),
                            (read/0.05/1024)));
                }
                return null;
            }
        }
    }

    // 辅助方法
    private static String formatFileSize(long bytes) {
        // 实现同前...
        return "0 KB";
    }

    private void showError(Throwable e) {
        // 错误处理实现...
    }
}
