package org.manager.WebSocket;

import org.java_websocket.server.WebSocketServer;

public class WebSocket extends WebSocketServer {
    // クライアントが接続したときに呼ばれる
    public MyWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message from client: " + message);
        // クライアントにメッセージを送信
        conn.send("Received: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started successfully");
    }

    public static void main(String[] args) {
        int port = 8887; // 任意のポート番号
        MyWebSocketServer server = new MyWebSocketServer(port);
        server.start();
        System.out.println("WebSocket server started on port: " + port);
    }
}
