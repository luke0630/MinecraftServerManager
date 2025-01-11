package org.manager;

import lombok.experimental.UtilityClass;

import java.net.InetAddress;
import java.net.UnknownHostException;

@UtilityClass
public class Utility {

    public Data.serverInfo isTargetServer(String host, String port) {
        for (Data.serverInfo info : Main.getData().getServerInfoList()) {
            try {
                // 入力アドレス1と登録アドレス2を正規化して比較
                InetAddress address1 = InetAddress.getByName(host);
                InetAddress address2 = InetAddress.getByName(info.host());

                if (address1.equals(address2) && port.equals(info.port())) {
                    return info;
                }
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
