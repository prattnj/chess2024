package service;

import chess.ChessGame;
import dataaccess.DatabaseManager;
import model.bean.GameBean;
import model.request.BaseRequest;
import model.request.JoinGameRequest;
import model.response.BaseResponse;
import server.BadRequestException;
import server.ForbiddenException;

import java.util.Objects;

/**
 * Allows the user to join the requested game as a player or viewer
 */
public class JoinGameService extends Service {

    @Override
    public BaseResponse doService(BaseRequest request, String authToken) throws Exception {

        JoinGameRequest req = (JoinGameRequest) request;

        String username = adao.find(authToken).getUsername();

        GameBean game = gdao.find(req.getGameID());

        // Verify that this game exists
        if (game == null) throw new BadRequestException("invalid game ID");

        // Verify that this team color is valid
        ChessGame.TeamColor color = null;
        if (req.getPlayerColor() != null) {
            if (req.getPlayerColor().equalsIgnoreCase("white")) color = ChessGame.TeamColor.WHITE;
            else if (req.getPlayerColor().equalsIgnoreCase("black")) color = ChessGame.TeamColor.BLACK;
            else throw new BadRequestException("invalid team color");
        }

        // Verify that this team color isn't taken
        Exception ex = new ForbiddenException("color is taken");
        if (color == ChessGame.TeamColor.WHITE && game.getWhiteUsername() != null && !Objects.equals(game.getWhiteUsername(), username)) throw ex;
        else if (color == ChessGame.TeamColor.BLACK && game.getBlackUsername() != null && !Objects.equals(game.getBlackUsername(), username)) throw ex;

        // Verify the AI's color isn't taken if applicable
        if (req.isAI()) {
            ex = new ForbiddenException("there's already a player in this game");
            if (color == ChessGame.TeamColor.WHITE && game.getBlackUsername() != null) throw ex;
            else if (color == ChessGame.TeamColor.BLACK && game.getWhiteUsername() != null) throw ex;
        }

        // This is a valid request
        if (color == ChessGame.TeamColor.WHITE) game.setWhiteUsername(username);
        else game.setBlackUsername(username);
        gdao.claimSpot(game.getGameID(), color, username);

        // Add the AI as the other player
        if (req.isAI()) {
            if (color == ChessGame.TeamColor.WHITE) game.setBlackUsername(DatabaseManager.AI_USERNAME);
            else game.setWhiteUsername(DatabaseManager.AI_USERNAME);
            color = color == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            gdao.claimSpot(game.getGameID(), color, DatabaseManager.AI_USERNAME);
        }

        return new BaseResponse();
    }
}
