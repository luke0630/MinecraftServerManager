package org.manager.WebApp.Backend;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class APIController {

    @GetMapping("/websocket-address")
    public String getWebsocketAddress(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        var address = Application.websocketController.getAddress();

        jsonObject.put("host", "localhost");
        jsonObject.put("port", address.getPort());
        return jsonObject.toString();
    }
}