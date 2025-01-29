package org.manager.WebSocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.manager.WebServer;

import java.net.InetSocketAddress;

// アプリにステータス情報を譲渡する
public class WebsocketController extends WebSocketServer {

    public WebsocketController(int port) {
        super(new InetSocketAddress(port));
public class APIWebsocketServer extends WebSocketServer {
    public APIWebsocketServer(InetSocketAddress address) {
        super(address);
    }

    public void sendMessage() {
        for(var connection : this.getConnections()) {
            connection.send(
                    Main.getServerDataJson().toString()
            );
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        webSocket.send(Main.getServerDataJson().toString());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}