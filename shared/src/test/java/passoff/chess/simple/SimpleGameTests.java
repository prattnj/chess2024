package passoff.chess.simple;

import chess.SimpleGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

public class SimpleGameTests {

    private SimpleGame sg;

    @BeforeEach
    public void setup() {
        sg = new SimpleGame();
    }

    @Test
    public void testMakeMove() {
        sg.makeMove(new int[]{1, 0, 3, 0});
    }

    @Test
    public void testValidMoves() {
        LinkedList<int[]> moves = sg.validMoves(1);
    }
}
