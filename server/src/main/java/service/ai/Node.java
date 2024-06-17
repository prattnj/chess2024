package service.ai;

import chess.ChessGame;
import chess.ChessPiece;
import chess.SimpleGame;
import util.Util;

import java.util.*;

public class Node {

    private final SimpleGame game;
    private final Collection<Node> children = new HashSet<>();

    public Node(SimpleGame game) {
        this.game = game;
    }

    public double evaluate(ChessGame.TeamColor turn) {
        double myValue = 0.0;
        double oppValue = 0.0;
        for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
            int myInstances = countInstances(type, turn);
            int oppInstances = countInstances(type, Util.oppositeColor(turn));
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

    public SimpleGame getGame() {
        return game;
    }

    public Collection<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    private int countInstances(ChessPiece.PieceType type, ChessGame.TeamColor turn) {
        char c = Util.getCharForType(type);
        if (turn == ChessGame.TeamColor.BLACK) c = Character.toLowerCase(c);
        return game.countInstances(c);
    }
}
