package service.ai;

import chess.ChessBoard;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private final ChessBoard board;
    private final List<Node> children = new ArrayList<>();

    public Node(ChessBoard board) {
        this.board = board;
    }

    public double evaluate(ChessGame.TeamColor turn) {
        return 0.0;
    }

    public List<Node> getChildren() {
        return children;
    }
}
