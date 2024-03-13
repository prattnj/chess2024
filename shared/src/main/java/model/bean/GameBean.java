package model.bean;

/**
 * Represents a Game entry in the database
 */
public class GameBean {

    private final int gameID;
    private String whiteUsername;
    private String blackUsername;
    private final String gameName;
    private String game;

    /**
     * Basic constructor
     * @param gameID A unique ID for this game
     * @param whiteUsername The username of the white player (can be null)
     * @param blackUsername The username of the black player (can be null)
     * @param gameName A unique name for this game
     * @param game A string representation of this game
     */
    public GameBean(int gameID, String whiteUsername, String blackUsername, String gameName, String game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    public int getGameID() {
        return gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public String getGameName() {
        return gameName;
    }

    public String getGame() {
        return game;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public void setGame(String game) {
        this.game = game;
    }
}
