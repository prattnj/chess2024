package service.ai;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import util.Util;

import java.util.HashMap;
import java.util.Map;

public class Node {

    private final ChessGame game;
    private final Map<ChessMove, Node> children = new HashMap<>();

    public Node(ChessGame game) {
        this.game = game;
    }

    public double evaluate(ChessGame.TeamColor turn) {
        double myValue = 0.0;
        double oppValue = 0.0;
        for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
            int myInstances = game.countInstances(type, turn);
            int oppInstances = game.countInstances(type, Util.oppositeColor(turn));
            double multiplier = switch (type) {
                case KING -> 0.0;
                case QUEEN -> 9.0;
                case ROOK -> 5.0;
                case BISHOP -> 4.0;
                case KNIGHT -> 3.0;
                case PAWN -> 1.0;
            };
            myValue += (myInstances * multiplier);
            oppValue += (oppInstances * multiplier);
        }
        return myValue - oppValue;
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
