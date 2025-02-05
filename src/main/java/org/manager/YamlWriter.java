package org.manager;

import org.manager.Data.Data;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

public class YamlWriter {
    static final String configName = "config.yml";
    public static void createConfig() {
        File configFile = new File(configName);
        if (!configFile.exists()) {
            try {
                InputStream inputStream = YamlWriter.class.getClassLoader().getResourceAsStream(configName);

                if (inputStream != null) {
                    File outputFile = new File(configName);

                    Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    inputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void readConfig(CallBack callBack) {
        try {
            FileReader reader = new FileReader(configName);

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(reader);

            Data resultData = Main.getData();

            if(!data.isEmpty()) {
                if (data.get("serverList") instanceof List<?> list) {
                    for (Object test : list) {
                        if (test instanceof String string) {
                            String[] splitString = string.split(":");
                            String host = splitString[0];
                            String port = splitString[1];
                            String name = splitString[2];
                            String displayName = splitString[3];

                            resultData.addServerInfoList(
                                    new Data.serverInfo(
                                        host, port, name, displayName
                                    )
                            );
                        }
                    }
                }
                resultData.setPort((int) data.get("port"));
                resultData.setWebsocket_port((int) data.get("websocket-port"));
                resultData.setWebsocket_api_port((int) data.get("websocket-api-port"));
            }

            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            callBack.onComplete();
        }
    }
}