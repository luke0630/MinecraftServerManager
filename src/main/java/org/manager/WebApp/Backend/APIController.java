package org.manager.WebApp.Backend;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class APIController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/websocket-address")
    public ResponseEntity<String> getWebsocketAddress(HttpServletRequest request, @RequestBody String payload) {
        JSONObject jsonResult = new JSONObject(payload);
        String host = request.getRemoteAddr();
        String port = String.valueOf(jsonResult.getInt("port"));

    @GetMapping("/websocket-address")
    public String getWebsocketAddress(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        var address = Application.websocketController.getAddress();

        jsonObject.put("host", "localhost");
        jsonObject.put("port", address.getPort());
        return jsonObject.toString();
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
        String host = request.getRemoteAddr();
        JSONObject jsonObject = new JSONObject();

        // クライアントのアドレスがサーバーのローカルアドレスと一致する場合は localhost を使用
        String websocketHost;
        try {
            if (InetAddress.getByName(host).isLoopbackAddress() || InetAddress.getByName(host).equals(InetAddress.getLocalHost())) {
                websocketHost = "localhost";
            } else {
                websocketHost = InetAddress.getLocalHost().getHostAddress();
            }
        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
            return "{}";
        }

        jsonObject.put("host", websocketHost);
        jsonObject.put("port", Main.getApiWebsocketServer().getPort());
        return jsonObject.toString();
    }
}