package org.manager.WebApp.Backend;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.manager.Data.Data;
import org.manager.Main;
import org.manager.Utility.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

@RestController
public class APIController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/websocket-address")
    public ResponseEntity<String> getWebsocketAddress(HttpServletRequest request, @RequestBody String payload) {
        JSONObject jsonResult = new JSONObject(payload);
        String host = request.getRemoteAddr();
        String port = String.valueOf(jsonResult.getInt("port"));

        Data.serverInfo serverInfo = Utility.isTargetServer(host, port);

        if(serverInfo != null) {
            JSONObject jsonObject = new JSONObject();

            // クライアントのアドレスがサーバーのローカルアドレスと一致する場合は localhost を使用
            String websocketHost;
            String clientIp = request.getRemoteAddr();
            String serverIp = getServerIp();

            if(clientIp.equals("127.0.0.1") || clientIp.equals("0:0:0:0:0:0:0:1") || clientIp.equals(serverIp)) {
                websocketHost = "localhost";
            } else {
                websocketHost = serverIp;
            }

            jsonObject.put("host", websocketHost);
            jsonObject.put("port", Main.getWebSocketServer().getPort());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(jsonObject.toString());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("");
    }

    @GetMapping("/api/status")
    public String getStatus() {
        JSONObject serverDataJson = Main.getServerDataJson();
        for(String serverName : serverDataJson.keySet()) {
            boolean isOnline = Main.getWebSocketServer().isOnline(serverName);
            serverDataJson.getJSONObject(serverName).put("isOnline", isOnline);
            if(!isOnline) {
                serverDataJson.getJSONObject(serverName).remove("serverData");
            }
        }
        return serverDataJson.toString();
    }
    @GetMapping("/api/websocket-address")
    public String getApiWebsocketAddress(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();

        // クライアントのアドレスがサーバーのローカルアドレスと一致する場合は localhost を使用
        String websocketHost;
        String clientIp = request.getRemoteAddr();
        String serverIp = getServerIp();

        if(clientIp.equals("127.0.0.1") || clientIp.equals("0:0:0:0:0:0:0:1") || clientIp.equals(serverIp)) {
            websocketHost = "localhost";
        } else {
            websocketHost = serverIp;
        }

        jsonObject.put("host", websocketHost);
        jsonObject.put("port", Main.getApiWebsocketServer().getPort());
        return jsonObject.toString();
    }

    @GetMapping("/api/all_players")
    public String getAllPlayers() {
        JSONObject jsonObject = new JSONObject();

        var map = Main.getWebSocketServer().getPlayers();
        for(Map.Entry<UUID, String> entry : map.entrySet()) {
            jsonObject.put(entry.getKey().toString(), entry.getValue());
        }
        return jsonObject.toString();
    }

    public static String getServerIp() {
        try {
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                 interfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = interfaces.nextElement();
                for (Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                     addresses.hasMoreElements(); ) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "127.0.0.1";  // 取得できなかった場合
    }
}