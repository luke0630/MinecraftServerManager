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

@Getter
public class LunchWebSocketServer extends WebSocketServer {
    public LunchWebSocketServer(InetSocketAddress address) {
        super(address);
    }
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    Map<String, WebSocket> serverList = new HashMap<>();

    public Boolean isOnline(String serverName) {
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
    }

    @Override
    public void onClose(org.java_websocket.WebSocket webSocket, int i, String s, boolean b) {
        for(Map.Entry<String, WebSocket> entry : serverList.entrySet()) {
            if(entry.getValue() == webSocket) {
                String serverName = entry.getKey();
                String serverDisplayName = Main.getServerDataJson()
                        .getJSONObject(serverName)
                        .getString("displayServerName");
                serverList.remove( entry.getKey() );
                Main.getServerDataJson().getJSONObject(serverName).put("isOnline", false);
                Main.getServerDataJson().getJSONObject(serverName).remove("serverData");
                sendUpdateWithoutTarget(
                        MessageType.MessageServer.UPDATE_CLOSED,
                        serverName,
                        serverDisplayName
                );
                Main.getApiWebsocketServer().sendMessage();
                logger.info(
                        entry.getKey() + " がクローズしました。"
                );
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

        String host = inetSocketAddress.getHostString();
        String port = String.valueOf(content.getInt("port"));

        JSONObject register_result = new JSONObject();

        Data.serverInfo serverInfo = Utility.isTargetServer(host, port);
        if(serverInfo != null) {
            logger.info(
                    serverInfo.name() + " が登録されました。"
            );
            serverList.put(serverInfo.name(), webSocket);

            register_result.put("name", serverInfo.name());

            sendUpdateWithoutTarget(
                    MessageType.MessageServer.UPDATE_REGISTERED,
                    serverInfo.name(),
                    serverInfo.displayName()
            );

            webSocket.send(MessageUtility.getResultResponse(MessageType.MessageServer.REGISTER_RESULT, register_result));
        } else {
            webSocket.close(1000, "不正なサーバーからのリクエスト");
        }
    }

    public void sendUpdateWithoutTarget(MessageType.MessageServer messageServer, String serverName, String displayName) {
        broadcastWithoutTarget(
                serverName,
                MessageUtility.getResultResponse(messageServer,
                        new JSONObject()
                                .put("name", serverName)
                                .put("displayName", displayName)
                )
        );
    }
}

class ServerNameData {
    String name;
    String displayName;

    public ServerNameData(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }
}