package ui;

import chess.*;
import model.AILevel;
import net.WSConnection;
import util.Util;
import websocket.commands.ConnectAI;
import websocket.commands.MakeMoveUC;
import websocket.commands.UserGameCommand;

import java.util.Collection;
import java.util.HashSet;

public class GameUI extends Client implements WSConnection.GameUI {

    private ChessGame game;
    private int gameID;
    private ChessGame.TeamColor color;

    public GameUI() {
        connection.assignGameUI(this);
    }

    public void start(int gameID, ChessGame.TeamColor color, boolean isAI) {
        this.color = color;
        this.gameID = gameID;

        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        if (isAI && color != null) {
            // prompt for difficulty level
            OUT.println("What difficulty level would you like for the AI?");
            AILevel level;
            while (true) {
                level = levelFromString(prompt("(e)asy, (m)edium, or (h)ard: "));
                if (level != null) break;
                else OUT.println("Invalid option, try again.");
            }
            command = new ConnectAI(authToken, gameID, level);
        }
        connection.send(gson.toJson(command));

        OUT.println("\nEntered in-game mode.");
        OUT.println("(" + HELP + ")");

        while(true) {
            basePrompt();
            String input = IN.nextLine().toLowerCase();
            String[] parts = input.split(" ");
            switch (parts[0]) {
                case "h", "help" -> help();
                case "d", "draw" -> redraw();
                case "l", "leave" -> {if (leave()) return;}
                case "m", "move" -> move(input);
                case "s", "show" -> show(input);
                case "r", "resign" -> resign();
                case "test" -> connection.send("testing");
                default -> OUT.println("Unknown command. " + HELP);
            }
        }
    }

    private void help() {
        OUT.println("Options:");
        OUT.println("\"h\", \"help\": See options");
        OUT.println("\"d\", \"draw\": Redraw the board");
        OUT.println("\"l\", \"leave\": Leave current game");
        OUT.println("\"m\", \"move\": Make a move");
        OUT.println("\"s\", \"show\": Show available moves for a piece");
        OUT.println("\"r\", \"resign\": Resign (game over)");
    }

    private void redraw() {
        drawBoard();
    }

    private boolean leave() {
        OUT.println("Leaving game.");
        connection.send(gson.toJson(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID)));
        return true;
    }

    private void move(String input) {

        // make sure this is a player
        if (color == null) {
            printError("You can't move as an observer.");
            return;
        }

        // make sure game is ongoing
        if (game.isOver()) {
            printError("The game is over. No moves can be made.");
            return;
        }

        // make sure it is this player's turn
        if (game.getTeamTurn() != color) {
            printError("It is not your turn.");
            return;
        }

        // determine the move
        String[] parts = input.split(" ");
        String startPosStr;
        String endPosStr;
        if (parts.length > 2) {
            startPosStr = parts[1];
            endPosStr = parts[2];
        } else {
            startPosStr = prompt("Enter the position of the piece to move (a5, d4...): ");
            endPosStr = prompt("Enter the position you'd like to move to: ");
        }
        if (!validatePosition(startPosStr) || !validatePosition(endPosStr)) {
            printError("Invalid position.");
            return;
        }
        ChessPosition start = new ChessPosition(startPosStr);
        ChessPosition end = new ChessPosition(endPosStr);
        Collection<ChessMove> validMoves = game.validMoves(start);

        // get promotion piece if applicable
        ChessPiece.PieceType promo = null;
        if (validMoves.contains(new ChessMove(start, end, ChessPiece.PieceType.QUEEN))) {
            char promoStr = prompt("Enter a piece type for pawn promotion (q, r, n, b): ").charAt(0);
            promo = switch (Character.toLowerCase(promoStr)) {
                case 'q' -> ChessPiece.PieceType.QUEEN;
                case 'r' -> ChessPiece.PieceType.ROOK;
                case 'n' -> ChessPiece.PieceType.KNIGHT;
                case 'b' -> ChessPiece.PieceType.BISHOP;
                default -> ChessPiece.PieceType.KING;
            };
            if (promo == ChessPiece.PieceType.KING) {
                printError("Invalid piece type.");
                return;
            }
        }

        // validate move
        ChessMove move = new ChessMove(start, end, promo);
        if (!validMoves.contains(move)) {
            printError("Invalid move.");
            return;
        }

        // make sure this piece belongs to this player
        if (game.getBoard().getPiece(move.getStartPosition()).getTeamColor() != color) {
            printError("That is not your piece.");
            return;
        }

        // move is valid
        connection.send(gson.toJson(new MakeMoveUC(authToken, gameID, move)));
    }

    private void show(String input) {

        // determine position to show
        String[] parts = input.split(" ");
        String posStr;
        if (parts.length > 1) posStr = parts[1];
        else posStr = prompt("Enter the position (i.e. a5, d4...) whose moves to show: ");
        if (!validatePosition(posStr)) {
            printError("Invalid position.");
            return;
        }
        ChessPosition pos = new ChessPosition(posStr);

        // draw board considering end positions
        Collection<ChessMove> possibleMoves = game.validMoves(pos);
        Collection<ChessPosition> endPositions = new HashSet<>();
        for (ChessMove m : possibleMoves) endPositions.add(m.getEndPosition());
        endPositions.add(pos);
        drawBoard(endPositions);
    }

    private void resign() {

        // make sure this is a player
        if (color == null) {
            printError("You can't resign as an observer.");
            return;
        }

        // make sure game is ongoing
        if (game.isOver()) {
            printError("You can't resign, the game is already over.");
            return;
        }

        // resignation is valid
        OUT.print("Are you sure you want to resign? (y/n): ");
        String resign = String.valueOf(IN.nextLine().charAt(0));
        if (!resign.equalsIgnoreCase("y")) {
            if (!resign.equalsIgnoreCase("n")) printError("Invalid input.");
            return;
        }
        connection.send(gson.toJson(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID)));
    }

    @Override
    public void setGame(ChessGame game) {
        // Sets game, but also checks for checkmate, stalemate, and check
        this.game = game;
        drawBoard();
        ChessGame.TeamColor color = game.getTeamTurn();
        ChessGame.TeamColor otherColor = Util.oppositeColor(color);
        if (game.isOver()) {
            if (game.isInCheckmate(color)) OUT.println("Checkmate! The " + otherColor + " player wins.");
            else if (game.isInStalemate(color)) OUT.println("Stalemate! The game is over.");
        } else if (game.isInCheck(color)) OUT.println("The " + color + " player is in check.");
        basePrompt();
    }

    @Override
    public void notify(String message, boolean isError) {
        if (isError) printError(message);
        else OUT.println(message);
        basePrompt();
    }

    // HELPER METHODS
    private void drawBoard() {
        drawBoard(new HashSet<>());
    }

    private void drawBoard(Collection<ChessPosition> endPositions) {
        if (game == null) return;
        OUT.print("\n");
        boolean isWhite = color != ChessGame.TeamColor.BLACK;
        printAlphaLabel(isWhite);
        printBoard(game.getBoard(), isWhite, endPositions);
        printAlphaLabel(isWhite);
        OUT.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private void printAlphaLabel(boolean isWhite) {
        // Prints 'a' - 'h' or vice versa depending on the color
        OUT.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + "   ");
        if (isWhite) for (int i = 0; i < 8; i++) OUT.print(" " + (char)('a' + i) + "\u2003");
        else for (int i = 7; i >= 0; i--) OUT.print(" " + (char)('a' + i) + "\u2003");
        OUT.println("   " + EscapeSequences.RESET_BG_COLOR);
    }

    private void printBoard(ChessBoard board, boolean isWhite, Collection<ChessPosition> endPositions) {
        boolean isLight = true;
        for (int i = 0; i < 8; i++) {
            int rowIndex = isWhite ? 8 - i : i + 1;
            OUT.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + rowIndex + " ");
            for (int j = 0; j < 8; j++) {

                // Determine indices
                int columnIndex = isWhite ? j + 1 : 8 - j;
                ChessPosition position = new ChessPosition(rowIndex, columnIndex);

                // Set the BG color
                if (isLight && endPositions.contains(position)) OUT.print(EscapeSequences.SET_BG_COLOR_GREEN);
                else if (isLight) OUT.print(EscapeSequences.SET_BG_COLOR_LIGHT_SQUARE);
                else if (endPositions.contains(position)) OUT.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                else OUT.print(EscapeSequences.SET_BG_COLOR_DARK_SQUARE);

                // Print the piece
                OUT.print(renderPiece(board.getPiece(position)));
                isLight = !isLight;
            }
            OUT.println(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + rowIndex + " " + EscapeSequences.RESET_BG_COLOR);
            isLight = !isLight;
        }
    }

    private String renderPiece(ChessPiece piece) {
        if (piece == null) return EscapeSequences.EMPTY;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()) {
                case KING -> EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_KING + EscapeSequences.SET_TEXT_COLOR_BLACK;
                case QUEEN -> EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_QUEEN + EscapeSequences.SET_TEXT_COLOR_BLACK;
                case ROOK -> EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_ROOK + EscapeSequences.SET_TEXT_COLOR_BLACK;
                case KNIGHT -> EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_KNIGHT + EscapeSequences.SET_TEXT_COLOR_BLACK;
                case BISHOP -> EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_BISHOP + EscapeSequences.SET_TEXT_COLOR_BLACK;
                case PAWN -> EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_PAWN + EscapeSequences.SET_TEXT_COLOR_BLACK;
            };
        } else {
            return switch (piece.getPieceType()) {
                case KING -> EscapeSequences.BLACK_KING;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case PAWN -> EscapeSequences.BLACK_PAWN;
            };
        }
    }

    private boolean validatePosition(String posStr) {
        // Determines if the given string represents a valid position
        if (posStr.length() != 2) return false;
        if (!Character.isAlphabetic(posStr.charAt(0)) || !Character.isDigit(posStr.charAt(1))) return false;
        posStr = posStr.toLowerCase();
        if (posStr.charAt(0) < 'a' || posStr.charAt(0) > 'h') return false;
        int i = Integer.parseInt(String.valueOf(posStr.charAt(1)));
        return i >= 1 && i <= 8;
    }

    private void basePrompt() {
        OUT.print(EscapeSequences.SET_TEXT_COLOR_BLUE + "\nchess> " + EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private AILevel levelFromString(String s) {
        return switch (s.toLowerCase()) {
            case "e", "easy" -> AILevel.EASY;
            case "m", "med", "medium" -> AILevel.MEDIUM;
            case "h", "hard" -> AILevel.HARD;
            default -> null;
        };
    }
}
