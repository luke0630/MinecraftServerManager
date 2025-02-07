package org.manager.Library.Data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DynamicServerData {
    public enum ServerStatus {
        STARTING, // サーバーが起動中
        RUNNING, // 起動完了（動作中）
    }

    private String serverName = "";
    private ServerStatus status;
    private String version;
    private List<PluginInfo> plugins;
    private List<PlayerData> players;

    @Setter
    @Getter
    public static class PluginInfo {
        private String pluginName = "";
        private String mainClass;
        private String description;
        private String version;
        private List<String> authors;
        private String website;
    }

    @Setter
    @Getter
    public static class PlayerData {
        private String uuid;
        private String name;
    }
}
