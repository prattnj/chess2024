package passoff.chess.simple.piece;

import chess.ChessPosition;
import org.junit.jupiter.api.Test;

import static passoff.chess.TestUtilities.validateMovesX;

public class KingMoveTests {

    @Test
    public void kingMoveUntilEdge() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | |K| | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(3, 6),
                new int[][]{{4, 6}, {4, 7}, {3, 7}, {2, 7}, {2, 6}, {2, 5}, {3, 5}, {4, 5}}
        );
    }


    @Test
    public void kingCaptureEnemy() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | |N|n| | | |
                        | | | |k| | | | |
                        | | |P|b|p| | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(3, 4),
                new int[][]{{4, 4}, {3, 5}, {2, 3}, {3, 3}, {4, 3}}
        );
    }


    @Test
    public void kingBlocked() {
        validateMovesX("""
                        | | | | | | |r|k|
                        | | | | | | |p|p|
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(8, 8),
                new int[][]{}
        );
    }

}
