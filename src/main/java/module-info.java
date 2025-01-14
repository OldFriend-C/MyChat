module com.example.chatui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpmime;
    requires org.apache.httpcomponents.httpcore;
    requires fastjson;
    requires lombok;
    requires com.rabbitmq.client;
    requires java.sql;

    opens com.example.chatui to javafx.fxml;
    exports com.example.chatui;
    exports com.example.chatui.aboutMessage;
    opens com.example.chatui.aboutMessage to javafx.fxml;
    exports com.example.chatui.aboutUser;
    opens com.example.chatui.aboutUser to javafx.fxml;
    exports com.example.chatui.basic;
    opens com.example.chatui.basic to javafx.fxml;
    exports com.example.chatui.friendRequest;
    exports com.example.chatui.MQChat;
}