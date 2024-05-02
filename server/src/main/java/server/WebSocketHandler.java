package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import model.bean.GameBean;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.MakeMoveUC;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;

@WebSocket
public class WebSocketHandler {

    private final Gson gson = new Gson();
    private final Map<Integer, Set<Session>> cache = new HashMap<>();
    private UserDAO udao;
    private GameDAO gdao;
    private static final ServerMessage.ServerMessageType LOAD_GAME = ServerMessage.ServerMessageType.LOAD_GAME;
    private static final ServerMessage.ServerMessageType ERROR = ServerMessage.ServerMessageType.ERROR;
    private static final ServerMessage.ServerMessageType NOTIFICATION = ServerMessage.ServerMessageType.NOTIFICATION;
    private Session root;
    private int currentGameID;
    private String currentUsername;
    private GameBean currentBean;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        root = session;

        try {
            // start database transaction
            udao = DAOFactory.getNewUserDAO();
            gdao = DAOFactory.getNewGameDAO();
            AuthTokenDAO adao = DAOFactory.getNewAuthTokenDAO();

            // validate command
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            if (command == null) {
                sendError("Invalid command.");
                return;
            }

            // validate authToken
            if (adao.find(command.getAuthString()) == null) {
                sendError("Invalid authToken.");
                return;
            }
            currentUsername = adao.find(command.getAuthString()).getUsername();

            // double check gameID
            currentBean = gdao.find(command.getGameID());
            if (currentBean == null) {
                sendError("Invalid gameID.");
                return;
            }

            // update current data
            currentGameID = command.getGameID();
            cache.computeIfAbsent(currentGameID, k -> new HashSet<>());
            cache.get(currentGameID).add(session);

            // note: if this is a join of any kind, it has already been
            // sent to the server via /games/join from the PostLoginUI client.
            switch (command.getCommandType()) {
                case CONNECT -> connect(gson.fromJson(message, UserGameCommand.class));
                case MAKE_MOVE -> makeMove(gson.fromJson(message, MakeMoveUC.class));
                case RESIGN -> resign();
                case LEAVE -> leave();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(int status, String reason) {}

    @OnWebSocketError
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    // COMMAND LOGIC
    private void connect(UserGameCommand command) throws DataAccessException {

        // send a LOAD_GAME back to the root client
        String game = currentBean.getGame();
        send(root, gson.toJson(new ServerMessage(LOAD_GAME, game)));

        // send a NOTIFICATION to all other clients
        String username = udao.find(currentUsername).getUsername();
        String message;
        ChessGame.TeamColor color = currentBean.getWhiteUsername().equals(username) ? ChessGame.TeamColor.WHITE : (
                currentBean.getBlackUsername().equals(username) ? ChessGame.TeamColor.BLACK : null
                );
        String observeMsg = username + " is now observing the game.";
        String joinedMsg = username + " joined the game as the " + color + " player.";
        broadcast(color == null ? observeMsg : joinedMsg);
    }

    private void makeMove(MakeMoveUC command) throws DataAccessException {

        ChessMove move = command.getMove();

        // make sure the person is a player in the game
        if (getColorString() == null) {
            sendError("You can't make a move as an observer.");
            return;
        }

        // make sure there is a second player
        if (!gameIsFull()) {
            sendError("Wait until another player joins before making a move.");
            return;
        }

        // make sure it is this player's turn
        ChessGame game = gson.fromJson(currentBean.getGame(), ChessGame.class);
        if (game.getTeamTurn() != getColor()) {
            sendError("It is not your turn.");
            return;
        }

        // make sure this piece belongs to this player
        if (game.getBoard().getPiece(move.getStartPosition()).getTeamColor() != getColor()) {
            sendError("That is not your piece.");
            return;
        }

        // validate/make the move
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            send(root, gson.toJson(new ServerMessage(ERROR, e.getMessage())));
            return;
        }

        // update the game in memory and in the database
        currentBean.setGame(gson.toJson(game));
        gdao.update(currentBean);

        // send a LOAD_GAME back to all clients in the game
        for (Session s : cache.get(currentGameID)) send(s, gson.toJson(new ServerMessage(LOAD_GAME, currentBean.getGame())));

        // send a NOTIFICATION to everyone except the root client
        broadcast("The " + getColorString() + " player made a move: " + move.getStartPosition().toString() + " -> " + move.getEndPosition().toString());
    }

    private void resign() throws DataAccessException {

        // make sure the person is a player in the game
        String color = getColorString();
        if (color == null) {
            sendError("You can't resign as an observer.");
            return;
        }

        // make sure the game is ongoing
        if (gson.fromJson(currentBean.getGame(), ChessGame.class).isOver()) {
            sendError("The game is already over.");
            return;
        }

        // make sure there is a second player
        if (!gameIsFull()) {
            sendError("You can't resign without an opponent.");
            return;
        }

        // send a NOTIFICATION to the root client that they resigned successfully
        send(root, gson.toJson(new ServerMessage(NOTIFICATION, "You have successfully resigned.")));

        // send a NOTIFICATION to everyone else that the root client resigned
        broadcast("The " + color + " player has resigned.");

        // mark game as over
        ChessGame game = gson.fromJson(currentBean.getGame(), ChessGame.class);
        game.setIsOver(true);
        currentBean.setGame(gson.toJson(game));
        gdao.update(currentBean);
    }

    private void leave() throws DataAccessException {

        // remove root client from the game
        cache.get(currentGameID).remove(root);

        // if this was a player, update the game in the database
        String color = getColorString();
        if (color != null) gdao.claimSpot(currentGameID, ChessGame.TeamColor.valueOf(color.toUpperCase()), null);

        // send a NOTIFICATION to all remaining clients
        broadcast(color == null ? currentUsername + " left the game." : currentUsername + " (the " + color + " player) left the game.");
    }

    // HELPER METHODS
    private void send(Session session, String message) {
        try {
            if (session.isOpen()) session.getRemote().sendString(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        for (Session s : cache.get(currentGameID)) if (!s.equals(root)) send(s, gson.toJson(new ServerMessage(NOTIFICATION, message)));
    }

    private void sendError(String message) {
        send(root, gson.toJson(new ServerMessage(ERROR, message)));
    }

    private String getColorString() {
        if (currentBean.getWhiteUsername() != null && Objects.equals(currentBean.getWhiteUsername(), currentUsername)) return "white";
        else if (currentBean.getBlackUsername() != null && Objects.equals(currentBean.getBlackUsername(), currentUsername)) return "black";
        else return null;
    }

    private ChessGame.TeamColor getColor() {
        if (currentBean.getWhiteUsername() != null && Objects.equals(currentBean.getWhiteUsername(), currentUsername)) return ChessGame.TeamColor.WHITE;
        else if (currentBean.getBlackUsername() != null && Objects.equals(currentBean.getBlackUsername(), currentUsername)) return ChessGame.TeamColor.BLACK;
        else return null;
    }

    private boolean gameIsFull() {
        return currentBean.getBlackUsername() != null && currentBean.getWhiteUsername() != null;
    }
}
