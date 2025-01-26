package org.manager;

import lombok.Getter;
import org.manager.WebApp.Backend.Application;
import org.manager.WebSocket.LunchWebSocketServer;

public class Main {
    @Getter
    private static Data data = new Data();
    @Getter
    private static LunchWebSocketServer webSocketServer;

    public static void main(String[] args) {
        YamlWriter.createConfig();
        YamlWriter.readConfig(() -> WebServer.startServer(data.getPort()));
        webSocketServer = LunchWebSocketServer.startServer();

        Application.main(args);
    }
}