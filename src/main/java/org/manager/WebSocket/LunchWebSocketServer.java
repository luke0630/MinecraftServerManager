package org.manager.WebSocket;

import lombok.Getter;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;
import org.luke.statusReporter.WebSocket.WebSocketClient;
import org.manager.Data;
import org.manager.Main;
import org.manager.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;

public class LunchWebSocketServer extends WebSocketServer {
    @Getter
    static Map<String, WebSocket> serverList = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static Boolean isOnline(String serverName) {
        return serverList.containsKey(serverName);
    }

    public void broadcast(String message) {
        for (WebSocket client : serverList.values()) {
            try {
                if(client.isOpen()) {
                    client.send(message);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void broadcastWithoutTarget(String target, String message) {
        for(Map.Entry<String, WebSocket> entry : serverList.entrySet()) {
            if(entry.getKey().equals(target)) continue;
            sendMessageToClient(entry.getKey(), message);
        }
    }

    @Override
    public void onOpen(org.java_websocket.WebSocket webSocket, ClientHandshake clientHandshake) {
        try {
            Thread.sleep(1000);
            webSocket.send("send");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClose(org.java_websocket.WebSocket webSocket, int i, String s, boolean b) {
        for(Map.Entry<String, WebSocket> entry : serverList.entrySet()) {
            if(entry.getValue() == webSocket) {
                String serverName = entry.getKey();
                String serverDisplayName = WebServer.getServerDataJson()
                        .getJSONObject(serverName)
                        .getString("displayServerName");
                broadcastWithoutTarget(serverName, String.format("CLOSED %s %s", serverName, serverDisplayName));
                serverList.remove( entry.getKey() );
                WebServer.getServerDataJson().getJSONObject(serverName).put("isOnline", false);
                WebServer.getServerDataJson().getJSONObject(serverName).remove("serverData");
                WebServer.updateInfo();
                System.out.println(entry.getKey() + " を削除しました");
                break;
            }
        }
    }

    @Override
    public void onMessage(org.java_websocket.WebSocket webSocket, String s) {
        System.out.println("websocket - Received message: " + s);

        JSONObject message = new JSONObject(s);

        WebSocketClient.MessageType type = WebSocketClient.MessageType.valueOf(message.getString("type"));
        JSONObject content = message.getJSONObject("content");

        String serverName = "";
        String serverDisplayName = "";
        if(type != WebSocketClient.MessageType.REGISTER) {
            boolean contain = false;
            for(Map.Entry<String, WebSocket> server : serverList.entrySet()) {
                if(server.getValue() == webSocket) {
                    serverName = server.getKey();
                    serverDisplayName = WebServer.getServerDataJson().getJSONObject(serverName)
                            .getString("displayServerName");
                    contain = true;
                    break;
                }
            }

            if(!contain) {
                webSocket.close(1000, "登録されていないサーバーからのリクエスト");
                return;
            }
        }

        WebServer.updateInfo();

        switch (type) {
            case REGISTER -> {
                InetSocketAddress inetSocketAddress = webSocket.getRemoteSocketAddress();

                String host = inetSocketAddress.getHostString();
                String port = String.valueOf(content.getInt("port"));

                Data.serverInfo serverInfo = Utility.isTargetServer(host, port);
                if(serverInfo != null) {
                    serverList.put(serverInfo.name(), webSocket);
                    webSocket.send("send");
                    String test = String.format("REGISTER %s %s", serverInfo.name(), serverInfo.displayName());
                    broadcastWithoutTarget(serverName, test);
                }
            }
            case STARTED -> {
                webSocket.send("send");
                broadcastWithoutTarget(serverName, String.format("STARTED %s %s", serverName, serverDisplayName));
            }
        }
    }

    @Override
    public void onError(org.java_websocket.WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {
        System.out.println("Server started successfully");
    }

    public LunchWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    public static void sendMessageToClient(String client, String message) {
        if(serverList.containsKey(client)) {
            serverList.get(client).send(message);
        }
    }

    public static LunchWebSocketServer startServer() {
        LunchWebSocketServer server = new LunchWebSocketServer(Main.getData().getWebsocket_port());
        server.start();

        System.out.println("WebSocket server started on port " + server.getPort());
        return server;
    }
}