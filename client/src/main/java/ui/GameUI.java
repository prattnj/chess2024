package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import net.WSConnection;
import webSocketMessages.userCommands.JoinPlayerUC;
import webSocketMessages.userCommands.MakeMoveUC;
import webSocketMessages.userCommands.UserGameCommand;

import java.util.Collection;
import java.util.HashSet;

public class GameUI extends Client implements WSConnection.GameUI {

    private ChessGame game;
    private int gameID;
    private boolean isPlayer;
    private ChessGame.TeamColor color;
    private static final UserGameCommand.CommandType JOIN_OBSERVER = UserGameCommand.CommandType.JOIN_OBSERVER;
    private static final UserGameCommand.CommandType LEAVE = UserGameCommand.CommandType.LEAVE;
    private static final UserGameCommand.CommandType RESIGN = UserGameCommand.CommandType.RESIGN;

    public GameUI() {
        connection.assignGameUI(this);
    }

    public void start(int gameID, ChessGame.TeamColor color, boolean isPlayer) {
        this.color = color;
        this.gameID = gameID;
        this.isPlayer = isPlayer;

        out.println("\nEntered in-game mode.");
        out.println("(" + HELP + ")");

        connection.send(gson.toJson(isPlayer ? new JoinPlayerUC(authToken, gameID, color) : new UserGameCommand(JOIN_OBSERVER, authToken, gameID)));

        while(true) {
            basePrompt();
            String input = in.nextLine().toLowerCase();
            String[] parts = input.split(" ");
            switch (parts[0]) {
                case "h", "help" -> help();
                case "d", "draw" -> redraw();
                case "l", "leave" -> {if (leave()) return;}
                case "m", "move" -> move(input);
                case "s", "show" -> show(input);
                case "r", "resign" -> resign();
                case "test" -> connection.send("testing");
                default -> out.println("Unknown command. " + HELP);
            }
        }
    }

    private void help() {
        out.println("Options:");
        out.println("\"h\", \"help\": See options");
        out.println("\"d\", \"draw\": Redraw the board");
        out.println("\"l\", \"leave\": Leave current game");
        out.println("\"m\", \"move\": Make a move");
        out.println("\"s\", \"show\": Show available moves for a piece");
        out.println("\"r\", \"resign\": Resign (game over)");
    }

    private void redraw() {
        drawBoard();
    }

    private boolean leave() {
        out.println("Leaving game.");
        connection.send(gson.toJson(new UserGameCommand(LEAVE, authToken, gameID)));
        return true;
    }

    private void move(String input) {

        // make sure this is a player
        if (!isPlayer) {
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
        if (!isPlayer) {
            printError("You can't resign as an observer.");
            return;
        }

        // make sure game is ongoing
        if (game.isOver()) {
            printError("You can't resign, the game is already over.");
            return;
        }

        // resignation is valid
        out.print("Are you sure you want to resign? (y/n): ");
        String resign = String.valueOf(in.nextLine().charAt(0));
        if (!resign.equalsIgnoreCase("y")) {
            if (!resign.equalsIgnoreCase("n")) printError("Invalid input.");
            return;
        }
        connection.send(gson.toJson(new UserGameCommand(RESIGN, authToken, gameID)));
    }

    @Override
    public void setGame(ChessGame game) {
        // Sets game, but also checks for checkmate, stalemate, and check
        this.game = game;
        drawBoard();
        ChessGame.TeamColor color = game.getTeamTurn();
        ChessGame.TeamColor otherColor = color == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        if (game.isOver()) {
            if (game.isInCheckmate(color)) out.println("Checkmate! The " + otherColor + " player wins.");
            else if (game.isInStalemate(color)) out.println("Stalemate! The game is over.");
        } else if (game.isInCheck(color)) out.println("The " + color + " player is in check.");
        basePrompt();
    }

    @Override
    public void notify(String message, boolean isError) {
        if (isError) printError(message);
        else out.println(message);
        basePrompt();
    }

    // HELPER METHODS
    private void drawBoard() {
        drawBoard(new HashSet<>());
    }

    private void drawBoard(Collection<ChessPosition> endPositions) {
        if (game == null) return;
        out.print("\n");
        boolean isWhite = color != ChessGame.TeamColor.BLACK;
        printAlphaLabel(isWhite);
        printBoard(game.getBoard().toString(), isWhite, endPositions);
        printAlphaLabel(isWhite);
        out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private void printAlphaLabel(boolean isWhite) {
        // Prints 'a' - 'h' or vice versa depending on the color
        out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + "   ");
        if (isWhite) for (int i = 0; i < 8; i++) out.print(" " + (char)('a' + i) + "\u2003");
        else for (int i = 7; i >= 0; i--) out.print(" " + (char)('a' + i) + "\u2003");
        out.println("   " + EscapeSequences.RESET_BG_COLOR);
    }

    private void printBoard(String board, boolean isWhite, Collection<ChessPosition> endPositions) {
        boolean isLight = true;
        for (int i = 0; i < 8; i++) {
            int rowIndex = isWhite ? 8 - i : i + 1;
            out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + rowIndex + " ");
            for (int j = 0; j < 8; j++) {

                // Determine indices
                int index = isWhite ? ((7 - i) * 8) + j : (i * 8) + (7 - j);
                int columnIndex = isWhite ? j + 1 : 8 - j;
                ChessPosition position = new ChessPosition(rowIndex, columnIndex);

                // Set the BG color
                if (isLight && endPositions.contains(position)) out.print(EscapeSequences.SET_BG_COLOR_GREEN);
                else if (isLight) out.print(EscapeSequences.SET_BG_COLOR_LIGHT_SQUARE);
                else if (endPositions.contains(position)) out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                else out.print(EscapeSequences.SET_BG_COLOR_DARK_SQUARE);

                // Print the piece
                out.print(renderPiece(board.charAt(index)));
                isLight = !isLight;
            }
            out.println(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + rowIndex + " " + EscapeSequences.RESET_BG_COLOR);
            isLight = !isLight;
        }
    }

    private String renderPiece(char c) {
        return switch (c) {
            case 'K' -> EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_KING + EscapeSequences.SET_TEXT_COLOR_BLACK;
            case 'Q' -> EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_QUEEN + EscapeSequences.SET_TEXT_COLOR_BLACK;
            case 'R' -> EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_ROOK + EscapeSequences.SET_TEXT_COLOR_BLACK;
            case 'N' -> EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_KNIGHT + EscapeSequences.SET_TEXT_COLOR_BLACK;
            case 'B' -> EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_BISHOP + EscapeSequences.SET_TEXT_COLOR_BLACK;
            case 'P' -> EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.BLACK_PAWN + EscapeSequences.SET_TEXT_COLOR_BLACK;
            case 'k' -> EscapeSequences.BLACK_KING;
            case 'q' -> EscapeSequences.BLACK_QUEEN;
            case 'r' -> EscapeSequences.BLACK_ROOK;
            case 'n' -> EscapeSequences.BLACK_KNIGHT;
            case 'b' -> EscapeSequences.BLACK_BISHOP;
            case 'p' -> EscapeSequences.BLACK_PAWN;
            default -> EscapeSequences.EMPTY;
        };
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
        out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + "\nchess> " + EscapeSequences.SET_TEXT_COLOR_WHITE);
    }
}
