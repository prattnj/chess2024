package passoff.chess.game;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FENTests {

    @Test
    public void testGameToFEN() throws InvalidMoveException {
        ChessGame game = new ChessGame();
        game.makeMove(new ChessMove(new ChessPosition(1, 2), new ChessPosition(3, 1), null));
        game.makeMove(new ChessMove(new ChessPosition(8, 2), new ChessPosition(6, 1), null));
        game.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
        game.makeMove(new ChessMove(new ChessPosition(7, 8), new ChessPosition(5, 8), null));
        game.makeMove(new ChessMove(new ChessPosition(3, 1), new ChessPosition(1, 2), null));
        game.makeMove(new ChessMove(new ChessPosition(6, 1), new ChessPosition(8, 2), null));
        Assertions.assertNotNull(game.toFEN());
    }

    @Test
    public void testFENToGame() {
        String fen = "rnbqknbr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        ChessGame game = new ChessGame(fen);
    }
}
