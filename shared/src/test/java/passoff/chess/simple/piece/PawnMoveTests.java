package passoff.chess.simple.piece;

import chess.ChessPiece;
import chess.ChessPosition;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static passoff.chess.TestUtilities.*;

public class PawnMoveTests {

    @Test
    public void pawnMiddleOfBoardWhite() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | |P| | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(4, 4),
                new int[][]{{5, 4}}
        );
    }

    @Test
    public void pawnMiddleOfBoardBlack() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | |p| | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(4, 4),
                new int[][]{{3, 4}}
        );
    }


    @Test
    public void pawnInitialMoveWhite() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | |P| | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(2, 5),
                new int[][]{{3, 5}, {4, 5}}
        );
    }

    @Test
    public void pawnInitialMoveBlack() {
        validateMovesX("""
                        | | | | | | | | |
                        | | |p| | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(7, 3),
                new int[][]{{6, 3}, {5, 3}}
        );
    }


    @Test
    public void pawnPromotionWhite() {
        validatePromotion("""
                        | | | | | | | | |
                        | | |P| | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(7, 3),
                new int[][]{{8, 3}}
        );
    }


    @Test
    public void edgePromotionBlack() {
        validatePromotion("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | |p| | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(2, 3),
                new int[][]{{1, 3}}
        );
    }


    @Test
    public void pawnPromotionCapture() {
        validatePromotion("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | |p| | | | | | |
                        |N| | | | | | | |
                        """,
                new ChessPosition(2, 2),
                new int[][]{{1, 1}, {1, 2}}
        );
    }


    @Test
    public void pawnAdvanceBlockedWhite() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | |n| | | | |
                        | | | |P| | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(4, 4),
                new int[][]{}
        );
    }

    @Test
    public void pawnAdvanceBlockedBlack() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | |p| | | | |
                        | | | |r| | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(5, 4),
                new int[][]{}
        );
    }


    @Test
    public void pawnAdvanceBlockedDoubleMoveWhite() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | |p| |
                        | | | | | | | | |
                        | | | | | | |P| |
                        | | | | | | | | |
                        """,
                new ChessPosition(2, 7),
                new int[][]{{3, 7}}
        );
    }

    @Test
    public void pawnAdvanceBlockedDoubleMoveBlack() {
        validateMovesX("""
                        | | | | | | | | |
                        | | |p| | | | | |
                        | | |p| | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(7, 3),
                new int[][]{}
        );
    }


    @Test
    public void pawnCaptureWhite() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | |r| |N| | | |
                        | | | |P| | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(4, 4),
                new int[][]{{5, 3}, {5, 4}}
        );
    }

    @Test
    public void pawnCaptureBlack() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | |p| | | | |
                        | | | |n|R| | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(4, 4),
                new int[][]{{3, 5}}
        );
    }

    private void validatePromotion(String boardText, ChessPosition startingPosition, int[][] endPositions) {
        char[][] board = loadBoardX(boardText);
        char testPiece = board[startingPosition.getRow() - 1][startingPosition.getColumn() - 1];
        var validMoves = new HashSet<int[]>();
        for (var endPosition : endPositions) {
            var end = new ChessPosition(endPosition[0], endPosition[1]);
            validMoves.add(new int[]{startingPosition.getRow() - 1, startingPosition.getColumn() - 1,
                    endPosition[0] - 1, endPosition[1] - 1, ChessPiece.PieceType.QUEEN.ordinal()});
            validMoves.add(new int[]{startingPosition.getRow() - 1, startingPosition.getColumn() - 1,
                    endPosition[0] - 1, endPosition[1] - 1, ChessPiece.PieceType.BISHOP.ordinal()});
            validMoves.add(new int[]{startingPosition.getRow() - 1, startingPosition.getColumn() - 1,
                    endPosition[0] - 1, endPosition[1] - 1, ChessPiece.PieceType.KNIGHT.ordinal()});
            validMoves.add(new int[]{startingPosition.getRow() - 1, startingPosition.getColumn() - 1,
                    endPosition[0] - 1, endPosition[1] - 1, ChessPiece.PieceType.ROOK.ordinal()});

        }

        validateMovesX(board, testPiece, startingPosition, validMoves);
    }

}
