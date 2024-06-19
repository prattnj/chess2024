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
        if (game.inCheckmate(turn == ChessGame.TeamColor.WHITE ? 1 : 2)) return Double.NEGATIVE_INFINITY; // player checkmate
        if (game.inCheckmate(turn == ChessGame.TeamColor.WHITE ? 2 : 1)) return Double.POSITIVE_INFINITY; // opp checkmate

        double myValue = 0.0;
        double oppValue = 0.0;
        for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
            double myInstances = countInstances(type, turn);
            double oppInstances = countInstances(type, Util.oppositeColor(turn));
            double multiplier = switch (type) { // DON'T MODIFY THESE
                case KING -> 0.0;
                case QUEEN -> 9.0;
                case ROOK -> 5.0;
                case BISHOP, KNIGHT -> 3.0;
                case PAWN -> 1.0;
            };
            myValue += (myInstances * multiplier);
            oppValue += (oppInstances * multiplier);
        }

        // calling validMoves twice here makes AI take twice as long
        double myMoves = 1;//game.validMoves(turn == ChessGame.TeamColor.WHITE ? 1 : 2).size();
        double oppMoves = 1;//game.validMoves(turn == ChessGame.TeamColor.WHITE ? 2 : 1).size();

        double pieceScore = (myValue - oppValue) / (myValue + oppValue);
        double pieceMultiplier = 1.0;

        double positionScore = 0.0;
        double positionMultiplier = 0.0;

        double mobilityScore = (myMoves - oppMoves) / (myMoves + oppMoves);
        double mobilityMultiplier = 0.0;

        return (pieceMultiplier * pieceScore) + (positionMultiplier * positionScore) + (mobilityMultiplier * mobilityScore);
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
