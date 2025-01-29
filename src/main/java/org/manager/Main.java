package org.manager;

import lombok.Getter;
import org.json.JSONObject;
import org.manager.WebApp.Backend.Application;
import org.manager.WebSocket.APIWebsocketServer;
import org.manager.WebSocket.LunchWebSocketServer;
import java.net.InetSocketAddress;

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

            InetSocketAddress address = new InetSocketAddress("localhost", 0);
            webSocketServer = new LunchWebSocketServer(address);
            webSocketServer.start();

            apiWebsocketServer = new APIWebsocketServer(address);
            apiWebsocketServer.start();
        });

        Application.main(args);
    }
}