package org.manager;

import lombok.Getter;
import org.json.JSONObject;
import org.manager.WebApp.Backend.Application;
import org.manager.WebSocket.APIWebsocketServer;
import org.manager.WebSocket.LunchWebSocketServer;
import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    @Getter
    private static Data data = new Data();
    @Getter
    private static LunchWebSocketServer webSocketServer;
    @Getter
    private static APIWebsocketServer apiWebsocketServer;
    @Getter
    private static JSONObject serverDataJson = new JSONObject();

    public static void main(String[] args) {
        YamlWriter.createConfig();
        YamlWriter.readConfig(() -> {
            for(Data.serverInfo info : Main.getData().getServerInfoList()) {
                JSONObject init = new JSONObject();
                init.put("isOnline", false);
                init.put("displayServerName", info.displayName());
                serverDataJson.put(info.name(), init);
            }

            try {
                webSocketServer = new LunchWebSocketServer(getAvailablePort());
                webSocketServer.start();

                Thread.sleep(1000);

                apiWebsocketServer = new APIWebsocketServer(getAvailablePort());
                apiWebsocketServer.start();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Application.main(args);
    }

    public static int getAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("空いているポートを取得できませんでした", e);
        }
    }
}