package org.manager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import org.json.JSONObject;
import org.manager.WebApp.Backend.Application;
import org.manager.WebSocket.LunchWebSocketServer;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class WebServer {
    @Getter
    static JSONObject serverDataJson = new JSONObject();

    public static void updateInfo() {
        Application.getWebsocketController().sendMessage(serverDataJson.toString());
    }

    public static void startServer(int port) {

        for(Data.serverInfo info : Main.getData().getServerInfoList()) {
            JSONObject init = new JSONObject();
            init.put("isOnline", false);
            init.put("displayServerName", info.displayName());
            serverDataJson.put(info.name(), init);
        }
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/status", new StatusHandler());
            server.createContext("/register", new RegisterHandler());
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String jsonResponse = "";

                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Server-Name");
                if ("POST".equals(exchange.getRequestMethod())) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    String serverName = exchange.getRequestHeaders().getFirst("Server-Name");

                    boolean isContains = false;
                    Data.serverInfo serverInfo = null;
                    for(Data.serverInfo info : Main.getData().getServerInfoList()) {
                        if(info.name().equals(serverName)) {
                            serverInfo = info;
                            isContains = true;
                            break;
                        }
                    }

                    if(isContains) {
                        JSONObject serverData = new JSONObject(requestBody);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("serverData", serverData);
                        jsonObject.put("isOnline", LunchWebSocketServer.isOnline(serverName));
                        jsonObject.put("displayServerName", serverInfo.displayName());
                        getServerDataJson().put(serverInfo.name(), jsonObject);
                        updateInfo();
                    } else {
                        InetSocketAddress address = exchange.getRemoteAddress();
                        System.out.printf("サーバーリストに存在しないサーバーからのリクエストなため無視します (%s:%d)%n",
                                address.getAddress().getHostAddress(),
                                address.getPort()
                        );
                    }
                } else {
                    jsonResponse = serverDataJson.toString();
                    for(String serverName : serverDataJson.keySet()) {
                        boolean isOnline = LunchWebSocketServer.isOnline(serverName);
                        serverDataJson.getJSONObject(serverName).put("isOnline", isOnline);
                        if(!isOnline) {
                            serverDataJson.getJSONObject(serverName).remove("serverData");
                        } else {
                            jsonResponse = serverDataJson.toString();
                        }
                    }
                }
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);

                try(OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes());
                }
            } catch (IOException e) {
                String response = "Internal server error.";
                exchange.sendResponseHeaders(500, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                throw new IOException(e);
            }
        }
    }
    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("POST".equals(exchange.getRequestMethod())) {
                    System.out.println(exchange.getResponseBody());
                    InetSocketAddress inetSocketAddress = exchange.getRemoteAddress();
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    JSONObject jsonResult = new JSONObject(requestBody);
                    String host = inetSocketAddress.getAddress().getHostAddress();
                    String port = String.valueOf(jsonResult.getInt("port"));

                    String response = "サーバーリストにあなたのサーバーアドレスは存在しなかったため認証されませんでした";

                    int status;
                    Data.serverInfo serverInfo = Utility.isTargetServer(host, port);
                    if(serverInfo != null) {
                        // レスポンスでwebsocketのサーバーのアドレスを教える
                        // クライアントのIPアドレスを取得
                        InetAddress clientAddress = inetSocketAddress.getAddress();
                        String websocketHost;
                        // クライアントのアドレスがサーバーのローカルアドレスと一致する場合は localhost を使用
                        if (clientAddress.isLoopbackAddress() || clientAddress.equals(InetAddress.getLocalHost())) {
                            websocketHost = "localhost";
                        } else {
                            websocketHost = InetAddress.getLocalHost().getHostAddress();
                        }

                        JSONObject serverAddress = new JSONObject();
                        InetSocketAddress websocket_address = Main.getWebSocketServer().getAddress();
                        serverAddress.put("host", websocketHost);
                        serverAddress.put("port", websocket_address.getPort());
                        serverAddress.put("name", serverInfo.name());
                        serverAddress.put("displayName", serverInfo.displayName());
                        response = serverAddress.toString();
                        status = 200;
                        System.out.printf("%s が認証されました。 (%s:%s)%n", serverInfo.name(), host, port);
                    } else {
                        status = 400;
                    }

                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(status, responseBytes.length);

                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(responseBytes);
                        os.flush();
                    }
                }
            } catch (Exception e) {
                System.err.println("Error occurred: " + e.getMessage());
                exchange.sendResponseHeaders(500, 0);
            }
        }
    }
}
