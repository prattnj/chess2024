package service.ai;

import chess.ChessGame;
import chess.ChessMove;

import java.util.HashMap;
import java.util.Map;

public class Node {

    private final ChessGame game;
    private final Map<ChessMove, Node> children = new HashMap<>();

    public Node(ChessGame game) {
        this.game = game;
    }

    public double evaluate(ChessGame.TeamColor turn) {
        return 0.0;
    }

    public ChessGame getGame() {
        return game;
    }

    public Map<ChessMove, Node> getChildren() {
        return children;
    }

    public void addChild(ChessMove m, Node child) {
        children.put(m, child);
    }
}
