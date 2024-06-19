package websocket.commands;

import model.AILevel;

import java.util.Objects;

public class ConnectAI extends UserGameCommand {

    private final AILevel level;

    public ConnectAI(String authToken, Integer gameID, AILevel level) {
        super(CommandType.CONNECT, authToken, gameID);
        this.level = Objects.requireNonNullElse(level, AILevel.HARD); // default to hard
    }

    public AILevel getLevel() {
        return level;
    }
}
