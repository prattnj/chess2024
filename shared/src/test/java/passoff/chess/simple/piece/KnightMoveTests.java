package passoff.chess.simple.piece;

import chess.ChessPosition;
import org.junit.jupiter.api.Test;

import static passoff.chess.TestUtilities.validateMovesX;

public class KnightMoveTests {

    @Test
    public void knightMiddleOfBoardWhite() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | |N| | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(5, 5),
                new int[][]{
                        {7, 6}, {6, 7}, {4, 7}, {3, 6}, {3, 4}, {4, 3}, {6, 3}, {7, 4},
                }
        );
    }

    @Test
    public void knightMiddleOfBoardBlack() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | |n| | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(5, 5),
                new int[][]{
                        {7, 6}, {6, 7}, {4, 7}, {3, 6}, {3, 4}, {4, 3}, {6, 3}, {7, 4},
                }
        );
    }


    @Test
    public void knightEdgeOfBoardLeft() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        |n| | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(4, 1),
                new int[][]{{6, 2}, {5, 3}, {3, 3}, {2, 2}}
        );
    }

    @Test
    public void knightEdgeOfBoardRight() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | |n|
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(3, 8),
                new int[][]{{1, 7}, {2, 6}, {4, 6}, {5, 7}}
        );
    }

    @Test
    public void knightEdgeOfBoardBottom() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | |N| | |
                        """,
                new ChessPosition(1, 6),
                new int[][]{{2, 4}, {3, 5}, {3, 7}, {2, 8}}
        );
    }

    @Test
    public void knightEdgeOfBoardTop() {
        validateMovesX("""
                        | | |N| | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(8, 3),
                new int[][]{{7, 5}, {6, 4}, {6, 2}, {7, 1}}
        );
    }


    @Test
    public void knightCornerOfBoardBottomRight() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | |N|
                        """,
                new ChessPosition(1, 8),
                new int[][]{{2, 6}, {3, 7}}
        );
    }

    @Test
    public void knightCornerOfBoardTopRight() {
        validateMovesX("""
                        | | | | | | | |N|
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(8, 8),
                new int[][]{{6, 7}, {7, 6}}
        );
    }

    @Test
    public void knightCornerOfBoardTopLeft() {
        validateMovesX("""
                        |n| | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(8, 1),
                new int[][]{{7, 3}, {6, 2}}
        );
    }

    @Test
    public void knightCornerOfBoardBottomLeft() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        |n| | | | | | | |
                        """,
                new ChessPosition(1, 1),
                new int[][]{{2, 3}, {3, 2}}
        );
    }


    @Test
    public void knightBlocked() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | |R| | | | |
                        | | | | | | |P| |
                        | | | | |N| | | |
                        | | |N| | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(5, 5),
                new int[][]{{3, 4}, {3, 6}, {4, 7}, {7, 6}, {6, 3}}
        );
    }


    @Test
    public void knightCaptureEnemy() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | |n| | | |
                        | | |N| | | | | |
                        | | | |P| |R| | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(5, 5),
                new int[][]{{7, 6}, {6, 7}, {4, 7}, {3, 6}, {3, 4}, {4, 3}, {6, 3}, {7, 4}}
        );
    }
}