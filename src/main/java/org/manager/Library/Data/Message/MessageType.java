package org.manager.Library.Data.Message;

public class MessageType {
    public enum MessageServer {
        REGISTER_RESULT,
        SEND_INFO,
        SEND_ALL_PLAYERS,
        UPDATE_REGISTERED,
        UPDATE_STARTED,
        UPDATE_CLOSED,
        UPDATE_INFO, // ほかのサーバーがSEND_INFO(情報が更新されたら)送信
    }
    public enum MessageClient {
        REGISTER,
        STARTED,
        SEND_INFO,
        SEND_ALL_PLAYERS,
    }
}
