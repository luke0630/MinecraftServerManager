package org.manager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class WebServer {
    public static class PlayerInfo {
        String name = "";
        String uuid = "";
    }

    public static void startServer(int port) {
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/status", new StatusHandler());
            server.createContext("/register", new RegisterHandler());
            server.createContext("/manager", new ManagerHandler());
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static JSONObject serverDataJson = new JSONObject();
    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("POST".equals(exchange.getRequestMethod())) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    System.out.println("Received data: " + requestBody);

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
                        serverDataJson.put(serverInfo.name(), serverData);
                    } else {
                        System.out.println("サーバーリストに存在しないサーバーからのリクエストです");
                    }

                    String response = "";
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    String jsonResponse = serverDataJson.toString();

                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, jsonResponse.length());
                    try(OutputStream os = exchange.getResponseBody()) {
                        os.write(jsonResponse.getBytes());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                String response = "Internal server error.";
                exchange.sendResponseHeaders(500, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }
    static class ManagerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
//            JSONObject jsonObject = new JSONObject(serverDataJson);
//
//            StringBuilder htmlResponse = new StringBuilder();
//            htmlResponse.append(
//                    "<html>\n" +
//                    "    <head>\n"
//            );
//
//            try {
//                for(String server_key : jsonObject.keySet()) {
//                    JSONObject mainObject = (JSONObject) jsonObject.get(server_key);
//                    htmlResponse.append(String.format("<h1>サーバー名: %s</h1>", server_key) );
//
//                    for(String key : mainObject.keySet()) {
//                        JSONObject value = (JSONObject) mainObject.get(key);
//                        switch(key) {
//                            case "players" -> {
//                                List<PlayerInfo> players = Reader.getPlayers(value);
//
//                                htmlResponse.append("<h2>プレイヤーリスト</h2>");
//                                htmlResponse.append(String.format("<h3>プレイヤー数: %d</h3>", players.size()));
//                                for(PlayerInfo info : players) {
//                                    htmlResponse.append(String.format("<p>%s  /  UUID: %s</p>", info.name, info.uuid));
//                                }
//                            }
//                            case "plugins" -> {
//
//                                htmlResponse.append("<h2>プラグインリスト</h2>");
//                                for (Reader.pluginsInfo pluginInfo : Reader.getPlugins(value)) {
//
//                                    htmlResponse.append(String.format("<h3>名前 : %s</h3>\n", pluginInfo.name()));
//                                    htmlResponse.append(String.format("<p>説明 : %s</p>\n", pluginInfo.description()));
//                                    htmlResponse.append(String.format("<p>バージョン : %s</p>\n", pluginInfo.version()));
//                                    htmlResponse.append(String.format("<p>作者 : %s</p>\n", pluginInfo.authors()));
//                                    htmlResponse.append(String.format("<p>ウェブサイト : %s</p>\n", pluginInfo.website()));
//                                    htmlResponse.append(String.format("<p>メインクラス : %s</p>\n", pluginInfo.main()));
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//
//            htmlResponse.append(
//                    "    </body>\n" +
//                    "</html>"
//            );
//
//            // レスポンスを返す
//            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
//            byte[] responseBytes = htmlResponse.toString().getBytes(StandardCharsets.UTF_8);
            String responseBytes = "";

            exchange.sendResponseHeaders(200, responseBytes.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes.toString().getBytes());
            }
        }
    }
    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InetSocketAddress inetSocketAddress = exchange.getRemoteAddress();
                String requestBody = new String(exchange.getRequestBody().readAllBytes());

                JSONObject jsonResult = new JSONObject(requestBody);
                String host = inetSocketAddress.getHostName();
                String port = String.valueOf(jsonResult.getInt("port"));

                String response = "サーバーリストにあなたのサーバーアドレスは存在しなかったため認証されませんでした";

                Data.serverInfo serverInfo = Utility.isTargetServer(host, port);
                if(serverInfo != null) {
                    // レスポンスでwebsocketのサーバーのアドレスを教える
                    // クライアントのIPアドレスを取得
                    InetAddress clientAddress = inetSocketAddress.getAddress();

                    // WebSocketサーバーのアドレスを準備
                    InetSocketAddress websocketAddress = Main.getWebSocketServer().getAddress();
                    String websocketHost;

                    // クライアントのアドレスがサーバーのローカルアドレスと一致する場合は localhost を使用
                    if (clientAddress.isLoopbackAddress() || clientAddress.equals(InetAddress.getLocalHost())) {
                        websocketHost = "localhost";
                    } else {
                        websocketHost = websocketAddress.getAddress().getHostAddress();
                    }

                    JSONObject serverAddress = new JSONObject();
                    InetSocketAddress websocket_address = Main.getWebSocketServer().getAddress();
                    serverAddress.put("host", websocketHost);
                    serverAddress.put("port", websocket_address.getPort());
                    serverAddress.put("name", serverInfo.name());
                    response = serverAddress.toString();
                }

                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes().length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }
}
