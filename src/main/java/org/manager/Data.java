package org.manager;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Data {
    private Integer port;
    private Integer websocket_port;
    private Integer websocket_api_port;
    public record serverInfo (String host, String port, String name, String displayName) {}

    @Getter
    private List<serverInfo> serverInfoList = new ArrayList<>();
    public void addServerInfoList(serverInfo info) {
        serverInfoList.add(info);
    }
}