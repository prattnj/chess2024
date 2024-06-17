package service.ai;

import chess.ChessGame;
import chess.ChessMove;
import model.AILevel;
import util.Util;

import java.util.Collection;

public class ChessAI {

    private int totalNodes;
    private final static int MAX_DEPTH = 4;
    private final AILevel level;
    private ChessGame.TeamColor maxPlayer;

    public ChessAI(AILevel level) {
        this.level = level;
    }

    public ChessMove getMove(ChessGame game, ChessGame.TeamColor turn) {
        if (game.getTeamTurn() != turn) return null; // not the AI's turn
        maxPlayer = turn;
        totalNodes = 0;
        Node root = new Node(game.toFEN());
        generateChildren(root, maxPlayer);
        double bestValue = Double.NEGATIVE_INFINITY;
        ChessMove bestMove = null;
        for (ChessMove m : root.getChildren().keySet()) {
            Node child = root.getChildren().get(m);
            double value = minimax(child, 1, Util.oppositeColor(turn), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            if (value > bestValue) {
                bestValue = value;
                bestMove = m;
            }
        }
        return bestMove;
    }

    // minimax with alpha-beta pruning
    private double minimax(Node n, int depth, ChessGame.TeamColor team, double alpha, double beta) {
        if (depth == MAX_DEPTH) return n.evaluate(team); // leaf node (evaluation function)

        generateChildren(n, team);

        if (team == maxPlayer) {
            double max = Double.NEGATIVE_INFINITY;
            for (Node child : n.getChildren().values()) {
                double value = minimax(child, depth + 1, Util.oppositeColor(maxPlayer), alpha, beta);
                if (value > max) max = value;
                if (max > alpha) alpha = max;
                if (beta <= alpha) break;
            }
            return max;
        } else {
            double min = Double.POSITIVE_INFINITY;
            for (Node child : n.getChildren().values()) {
                double value = minimax(child, depth + 1, maxPlayer, alpha, beta);
                if (value < min) min = value;
                if (min < beta) beta = min;
                if (beta <= alpha) break;
            }
            return min;
        }
    }

    private void generateChildren(Node n, ChessGame.TeamColor turn) {
        Collection<ChessMove> moves = n.getGame().validMoves(turn);
        for (ChessMove m : moves) {
            ChessGame game = n.getGame();
            game.makeMoveForce(m);
            Node child = new Node(game.toFEN());
            n.addChild(m, child);
            totalNodes++;
        }
    }
}
