package service.ai;

import chess.ChessGame;
import chess.ChessMove;
import model.AILevel;

public class ChessAI {

    Node root;
    int maxDepth = 3;

    public ChessMove getMove(ChessGame game, ChessGame.TeamColor turn, AILevel level) {
        double bestValue = minimax(root, 1, true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        return null;
    }

    // minimax with alpha-beta pruning
    private double minimax(Node n, int depth, boolean isMaxPlayer, double alpha, double beta) {
        // leaf node (evaluation function)
        if (depth == maxDepth) return n.evaluate();

        if (isMaxPlayer) {
            double max = Double.NEGATIVE_INFINITY;
            for (Node child : n.getChildren()) {
                double value = minimax(child, depth + 1, false, alpha, beta);
                if (value > max) max = value;
                if (max > alpha) alpha = max;
                if (beta <= alpha) break;
            }
            return max;
        } else {
            double min = Double.POSITIVE_INFINITY;
            for (Node child : n.getChildren()) {
                double value = minimax(child, depth + 1, true, alpha, beta);
                if (value < min) min = value;
                if (min < beta) beta = min;
                if (beta <= alpha) break;
            }
            return min;
        }
    }
}
