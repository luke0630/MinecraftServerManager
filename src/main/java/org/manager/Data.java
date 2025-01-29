package org.manager;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Data {
    private Integer port;
    public record serverInfo (String host, String port, String name, String displayName) {}

    @Getter
    private List<serverInfo> serverInfoList = new ArrayList<>();
    public void addServerInfoList(serverInfo info) {
        serverInfoList.add(info);
    }
}