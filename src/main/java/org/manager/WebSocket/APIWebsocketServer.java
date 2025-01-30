package org.manager.WebSocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.manager.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class APIWebsocketServer extends WebSocketServer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public APIWebsocketServer(int port) {
        super(new InetSocketAddress(port));
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
        logger.info("API用websocketが開始しました。 PORT:" + getPort());
    }
}