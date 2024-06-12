package model.request;

/**
 * A request used to join a game
 */
public class JoinGameRequest extends BaseRequest {

    private final String playerColor;
    private final int gameID;
    private final boolean isAI;

    /**
     * Basic constructor
     * @param playerColor The color that this user wants to play as
     * @param gameID The game that the user is attempting to join
     */
    public JoinGameRequest(String playerColor, int gameID, boolean isAI) {
        this.playerColor = playerColor;
        this.gameID = gameID;
        this.isAI = isAI;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    public boolean isAI() {
        return isAI;
    }

    @Override
    public boolean isComplete() {
        return playerColor != null;
    }
}
