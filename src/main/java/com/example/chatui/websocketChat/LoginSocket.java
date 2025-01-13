package com.example.chatui.websocketChat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//用于登录连接,登录完成后断开连接
public class LoginSocket {
    private final WebSocket webSocket;
    private final ExecutorService executorService;

    public LoginSocket(URI uri) {
        this.executorService = Executors.newSingleThreadExecutor();
        this.webSocket = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(uri, new WebSocketListener())
                .join();
    }

    private class WebSocketListener implements WebSocket.Listener {
        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("WebSocket connection opened.");
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            System.out.println("Received text message: " + data);
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
            System.out.println("Received binary message.");
            return WebSocket.Listener.super.onBinary(webSocket, data, last);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.err.println("WebSocket error: " + error.getMessage());
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            System.out.println("WebSocket connection closed.");
            return null;
        }
    }

    public void sendText(String message) {
        webSocket.sendText(message, true);
    }

    public void sendBinary(byte[] data) {
        webSocket.sendBinary(ByteBuffer.wrap(data), true);
    }

    public void close() {
        webSocket.sendClose(1000, "Client closed connection.");
        executorService.shutdown();
    }
}
