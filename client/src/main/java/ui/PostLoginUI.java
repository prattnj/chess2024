package ui;

import chess.ChessGame;
import model.request.BaseRequest;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.response.BaseResponse;
import model.response.CreateGameResponse;
import model.response.ListGamesObj;
import model.response.ListGamesResponse;
import util.Util;

import java.util.Arrays;
import java.util.List;

public class PostLoginUI extends PreLoginUI {

    private List<ListGamesObj> allGames = null;

    public void start() {

        OUT.println("Logged in successfully.");
        OUT.println("(" + HELP + ")");

        updateGames();

        while(true) {

            OUT.print(EscapeSequences.SET_TEXT_COLOR_GREEN + "\nchess> " + EscapeSequences.SET_TEXT_COLOR_WHITE);
            String input = IN.nextLine().toLowerCase();
            String[] parts = input.split(" ");
            switch (parts[0]) {
                case "h", "help" -> help();
                case "c", "create" -> create(input);
                case "ls", "list" -> list();
                case "j", "join" -> join(input, false);
                case "jai", "joinai" -> join(input, true);
                case "o", "observe" -> observe(input);
                case "lg", "logout" -> {if (logout()) return;}
                case "q", "quit" -> quit();
                default -> OUT.println("Unknown command. " + HELP);
            }
        }
    }

    private void help() {
        OUT.println("Options:");
        OUT.println("\"h\", \"help\": See options");
        OUT.println("\"c <name>\", \"create <name>\": Create a new game");
        OUT.println("\"ls\", \"list\": List all existing games");
        OUT.println("\"j <gameID>\", \"join <gameID>\": Join an existing game specified by the given game ID");
        OUT.println("\"jAI <gameID>\", \"joinAI <gameID>\": Join an existing game with an AI player");
        OUT.println("\"o <gameID>\", \"observe <gameID>\": Observe a game specified by the given game ID");
        OUT.println("\"lg\", \"logout\": Logout");
        OUT.println("\"q\", \"quit\": Exit the program");
    }

    private boolean logout() {
        // send logout request to server
        BaseResponse response = server.logout(authToken);
        if (response.getMessage() == null) {
            authToken = null;
            OUT.println("Logged out successfully.");
        } else printError(response.getMessage());
        return response.getMessage() == null;
    }

    private void create(String input) {

        // determine game name
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            OUT.println("Must specify a game name.");
            return;
        }

        // send create request to server and print gameID if successful
        BaseRequest request = new CreateGameRequest(parts[1]);
        BaseResponse response = server.create(request, authToken);
        if (response.getMessage() == null) {
            int gameID = ((CreateGameResponse) response).getGameID();
            OUT.println("Game successfully created with ID " + gameID);
            updateGames();
        } else printError(response.getMessage());
    }

    private void list() {
        updateGames();
        if (allGames.isEmpty()) OUT.println("There are currently no games.");
        else for (ListGamesObj game : allGames) printGame(game);
    }

    private void join(String input, boolean isAI) {

        // determine gameID
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            OUT.println("Must specify a game ID. Enter 'ls' to see games.");
            return;
        }
        int gameID = Integer.parseInt(parts[1]);

        // finish building request
        ChessGame.TeamColor teamColor = Util.getColorForString(prompt("Would you like to play as (b)lack or (w)hite? "));
        if (teamColor == null) {
            OUT.println("Invalid color.");
            return;
        }

        joinOrObserve(teamColor, gameID, isAI);
    }

    private void observe(String input) {

        // determine gameID
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            OUT.println("Must specify a game ID. Enter 'ls' to see games.");
            return;
        }
        int gameID = Integer.parseInt(parts[1]);

        joinOrObserve(null, gameID, false);
    }

    private void joinOrObserve(ChessGame.TeamColor color, int gameID, boolean isAI) {

        if (color != null) {
            // send join request to server
            JoinGameRequest request = new JoinGameRequest(Util.getStringForColor(color), gameID, isAI);
            BaseResponse response = server.join(request, authToken);
            if (response.getMessage() == null) {
                updateGames();
                OUT.println("Successfully joined game " + gameID);
                new GameUI().start(gameID, color, isAI);
            } else printError(response.getMessage());
        } else {
            updateGames();
            boolean validID = false;
            for (ListGamesObj lgo : allGames)
                if (lgo.getGameID() == gameID) {
                    if (lgo.getBlackUsername().equals(Util.AI_USERNAME) || lgo.getWhiteUsername().equals(Util.AI_USERNAME))
                        isAI = true;
                    validID = true;
                    break;
                }
            if (!validID) OUT.println("Invalid gameID. Try again.");
            else {
                OUT.println("Successfully joined game " + gameID);
                new GameUI().start(gameID, null, isAI);
            }
        }

    }

    // HELPER METHODS
    private void updateGames() {

        // send list games request to server
        BaseResponse listResp = server.list(authToken);
        if (listResp.getMessage() == null) allGames = Arrays.asList(((ListGamesResponse) listResp).getGames());
        else {
            printError("Server error, exiting. Try again later.");
            System.exit(0);
        }
    }

    private void printGame(ListGamesObj game) {
        OUT.println("ID: " + game.getGameID());
        OUT.println("Name: " + game.getGameName());
        OUT.println("White: " + (game.getWhiteUsername() == null ? "" : game.getWhiteUsername()));
        OUT.println("Black: " + (game.getBlackUsername() == null ? "" : game.getBlackUsername()));
        OUT.print("\n");
    }
}
