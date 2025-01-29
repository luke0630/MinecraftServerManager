package org.manager.WebApp.Backend;

import lombok.Getter;
import org.manager.WebApp.Backend.Websocket.WebsocketController;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .properties("server.port=8081")
                .run(args);
    }
}
