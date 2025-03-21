package com.example.chatui.aboutMessage;


import javafx.scene.Node;
import javafx.scene.text.Text;

public class TextElemNode implements ContentElemNode {

    private String content;

    public TextElemNode(String content) {
        this.content = content;
    }

    @Override
    public Node toUi() {
        Text label = new Text (content);
        label.setStyle("-fx-font-size: 18;");
        label.setWrappingWidth(600); // 设置换行宽度
        label.setMouseTransparent(false);
        label.setFocusTraversable(true);
        return label;
    }
}