package websocket.messages;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    private String message;
    private String game;
    private String errorMessage;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String message){
        this.serverMessageType = type;
        if (type == ServerMessageType.NOTIFICATION) this.message = message;
        else if (type == ServerMessageType.ERROR) this.errorMessage = message;
        else this.game = message;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getMessage() {
        return switch (serverMessageType) {
            case NOTIFICATION -> message;
            case ERROR -> errorMessage;
            case LOAD_GAME -> game;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerMessage that = (ServerMessage) o;
        return serverMessageType == that.serverMessageType && Objects.equals(message, that.message) && Objects.equals(game, that.game) && Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverMessageType, message, game, errorMessage);
    }
}