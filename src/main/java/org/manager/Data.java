package org.manager;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Data {
    private Integer websocket_port;

    private Integer port;
    public record serverInfo (String host, String port, String name) {}

    @Getter
    private List<serverInfo> serverInfoList = new ArrayList<>();
    public void addServerInfoList(serverInfo info) {
        serverInfoList.add(info);
    }
}