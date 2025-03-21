package com.example.chatui.aboutMessage;


import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class EmojiElemNode implements ContentElemNode {

    private String url;

    public EmojiElemNode(String url) {
        this.url = url;
    }

    @Override
    public Node toUi() {
        ImageView imageView = new ImageView(url);
        imageView.setFitWidth(18);
        imageView.setFitHeight(18);
        imageView.setStyle("-fx-background-color: transparent;" +
                "-fx-border-color: transparent;");
        return imageView;
    }
}