package service.ai;

import chess.ChessGame;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private final ChessGame game;
    private final List<Node> children = new ArrayList<>();

    public Node(ChessGame game) {
        this.game = game;
    }

    public double evaluate(ChessGame.TeamColor turn) {
        return 0.0;
    }

    public List<Node> getChildren() {
        return children;
    }
}
