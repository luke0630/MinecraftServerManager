package org.manager.WebApp.Backend;

import org.manager.Main;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .properties("server.port=" + Main.getData().getPort())
                .run(args);
    }
}
