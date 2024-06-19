package service;

import chess.ChessGame;
import chess.ChessMove;
import model.AILevel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.ai.ChessAI;

public class AITests {

    private final ChessAI ai = new ChessAI(AILevel.HARD);
    private final ChessGame game = new ChessGame();

    @Test
    public void aiTest() {
        ChessMove move = ai.getMove(game, ChessGame.TeamColor.WHITE);
        Assertions.assertNotNull(move);
    }
}
