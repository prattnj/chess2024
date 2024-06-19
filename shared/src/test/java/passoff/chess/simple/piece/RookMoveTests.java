package passoff.chess.simple.piece;

import chess.ChessPosition;
import org.junit.jupiter.api.Test;

import static passoff.chess.TestUtilities.validateMovesX;

public class RookMoveTests {

    @Test
    public void rookMoveUntilEdge() {

        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | |R| | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(2, 3),
                new int[][]{
                        {2, 4}, {2, 5}, {2, 6}, {2, 7}, {2, 8},
                        {2, 2}, {2, 1},
                        {1, 3},
                        {3, 3}, {4, 3}, {5, 3}, {6, 3}, {7, 3}, {8, 3},
                }
        );
    }


    @Test
    public void rookCaptureEnemy() {
        validateMovesX("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        |N| | | | | | | |
                        |r| | | | |B| | |
                        | | | | | | | | |
                        |q| | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(4, 1),
                new int[][]{
                        {5, 1},
                        {3, 1},
                        {4, 2}, {4, 3}, {4, 4}, {4, 5}, {4, 6},
                }
        );
    }


    @Test
    public void rookBlocked() {
        validateMovesX("""
                        | | | | | | |n|r|
                        | | | | | | | |p|
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
