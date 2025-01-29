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
    }
}