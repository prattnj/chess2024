package passoff.chess;

import chess.*;
import org.junit.jupiter.api.Assertions;

import java.util.*;

public class TestUtilities {
    static public void validateMoves(String boardText, ChessPosition startPosition, int[][] endPositions) {
        var board = loadBoard(boardText);
        var testPiece = board.getPiece(startPosition);
        var validMoves = loadMoves(startPosition, endPositions);
        validateMoves(board, testPiece, startPosition, validMoves);
    }

    static public void validateMovesX(String boardText, ChessPosition startPosition, int[][] endPositions) {
        char[][] board = loadBoardX(boardText);
        char testPiece = board[startPosition.getRow() - 1][startPosition.getColumn() - 1];
        Set<int[]> validMoves = loadMovesX(startPosition, endPositions);
        validateMovesX(board, testPiece, startPosition, validMoves);
    }

    static public void validateMoves(ChessBoard board, ChessPiece testPiece, ChessPosition startPosition, Set<ChessMove> validMoves) {
        var pieceMoves = new HashSet<>(testPiece.pieceMoves(board, startPosition));
        assertCollectionsEquals(validMoves, pieceMoves, "Wrong moves");
    }

    static public void validateMovesX(char[][] board, char testPiece, ChessPosition startPosition, Set<int[]> validMoves) {
        Collection<int[]> pieceMoves = switch (Character.toLowerCase(testPiece)) {
            case 'k' -> SimpleGame.kingMoves(board, startPosition.getRow() - 1, startPosition.getColumn() - 1);
            case 'q' -> SimpleGame.queenMoves(board, startPosition.getRow() - 1, startPosition.getColumn() - 1);
            case 'r' -> SimpleGame.rookMoves(board, startPosition.getRow() - 1, startPosition.getColumn() - 1);
            case 'b' -> SimpleGame.bishopMoves(board, startPosition.getRow() - 1, startPosition.getColumn() - 1);
            case 'n' -> SimpleGame.knightMoves(board, startPosition.getRow() - 1, startPosition.getColumn() - 1);
            case 'p' -> SimpleGame.pawnMoves(board, startPosition.getRow() - 1, startPosition.getColumn() - 1);
            default -> null;
        };
        assertCollectionsEqualsX(validMoves, pieceMoves, "Wrong moves");
    }

    static public <T> void assertCollectionsEquals(Collection<T> first, Collection<T> second, String message) {
        Assertions.assertEquals(new HashSet<>(first), new HashSet<>(second), message);
        Assertions.assertEquals(first.size(), second.size(), "Collections not the same size");
    }

    static public void assertCollectionsEqualsX(Collection<int[]> first, Collection<int[]> second, String message) {
        HashSet<List<Integer>> firstSet = new HashSet<>();
        for (int[] arr : first) {
            firstSet.add(Arrays.stream(arr).boxed().toList());
        }
        HashSet<List<Integer>> secondSet = new HashSet<>();
        for (int[] arr : second) {
            secondSet.add(Arrays.stream(arr).boxed().toList());
        }
        Assertions.assertEquals(firstSet, secondSet, message);
        Assertions.assertEquals(firstSet.size(), secondSet.size(), "Collections not the same size");
    }

    final static Map<Character, ChessPiece.PieceType> CHAR_TO_TYPE_MAP = Map.of(
            'p', ChessPiece.PieceType.PAWN,
            'n', ChessPiece.PieceType.KNIGHT,
            'r', ChessPiece.PieceType.ROOK,
            'q', ChessPiece.PieceType.QUEEN,
            'k', ChessPiece.PieceType.KING,
            'b', ChessPiece.PieceType.BISHOP);

    public static ChessBoard loadBoard(String boardText) {
        var board = new ChessBoard();
        int row = 8;
        int column = 1;
        for (var c : boardText.toCharArray()) {
            switch (c) {
                case '\n' -> {
                    column = 1;
                    row--;
                }
                case ' ' -> column++;
                case '|' -> {
                }
                default -> {
                    ChessGame.TeamColor color = Character.isLowerCase(c) ? ChessGame.TeamColor.BLACK
                            : ChessGame.TeamColor.WHITE;
                    var type = CHAR_TO_TYPE_MAP.get(Character.toLowerCase(c));
                    var position = new ChessPosition(row, column);
                    var piece = new ChessPiece(color, type);
                    board.addPiece(position, piece);
                    column++;
                }
            }
        }
        return board;
    }

    public static char[][] loadBoardX(String boardText) {
        var board = new char[8][8];
        int row = 8;
        int column = 1;
        for (var c : boardText.toCharArray()) {
            switch (c) {
                case '\n' -> {
                    column = 1;
                    row--;
                }
                case ' ' -> column++;
                case '|' -> {
                }
                default -> {
                    board[row - 1][column - 1] = c;
                    column++;
                }
            }
        }
        return board;
    }

    public static Set<ChessMove> loadMoves(ChessPosition startPosition, int[][] endPositions) {
        var validMoves = new HashSet<ChessMove>();
        for (var endPosition : endPositions) {
            validMoves.add(new ChessMove(startPosition,
                    new ChessPosition(endPosition[0], endPosition[1]), null));
        }
        return validMoves;
    }

    public static Set<int[]> loadMovesX(ChessPosition startPosition, int[][] endPositions) {
        var validMoves = new HashSet<int[]>();
        for (var endPosition : endPositions) {
            validMoves.add(new int[]{startPosition.getRow() - 1, startPosition.getColumn() - 1,
                    endPosition[0] - 1, endPosition[1] - 1});
        }
        return validMoves;
    }

    public static void assertMoves(ChessGame game, Set<ChessMove> validMoves, ChessPosition position) {
        var generatedMoves = game.validMoves(position);
        var actualMoves = new HashSet<>(generatedMoves);
        Assertions.assertEquals(generatedMoves.size(), actualMoves.size(), "Duplicate move");
        Assertions.assertEquals(validMoves, actualMoves,
                "ChessGame validMoves did not return the correct moves");
    }

    public static void assertMovesX(SimpleGame game, Set<int[]> validMoves, ChessPosition position) {
        var generatedMoves = game.validMoves(position.getRow() - 1, position.getColumn() - 1);
        var actualMoves = new HashSet<>(generatedMoves);
        Assertions.assertEquals(generatedMoves.size(), actualMoves.size(), "Duplicate move");
        Assertions.assertEquals(validMoves, actualMoves,
                "ChessGame validMoves did not return the correct moves");
    }
}
