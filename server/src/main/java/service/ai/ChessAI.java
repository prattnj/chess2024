package service.ai;

import chess.ChessGame;
import chess.ChessMove;
import model.AILevel;
import util.Util;

public class ChessAI {

    private Node root;
    private int totalNodes;
    private final static int MAX_DEPTH = 3;
    private final AILevel level;
    private ChessGame.TeamColor maxPlayer;

    public ChessAI(AILevel level) {
        this.level = level;
    }

    public ChessMove getMove(ChessGame game, ChessGame.TeamColor turn) {
        if (game.getTeamTurn() != turn) return null; // not the AI's turn
        maxPlayer = turn;
        generateTree(game, turn);
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

    private void generateTree(ChessGame game, ChessGame.TeamColor turn) {
        System.gc(); // remind the JVM to free up heap space from the last tree
        root = new Node(game);
        totalNodes = 0;
        generateChildren(root, maxPlayer, 1);
    }

    // recursive helper function for tree generation
    private void generateChildren(Node n, ChessGame.TeamColor turn, int depth) {
        if (depth > MAX_DEPTH) return;
        for (ChessMove m : n.getGame().validMoves(turn)) {
            ChessGame game = n.getGame().clone();
            try {
                game.makeMove(m);
            } catch (Exception e) { // shouldn't happen
                continue;
            }
            Node child = new Node(game);
            n.addChild(m, child);
            totalNodes++;
            generateChildren(child, Util.oppositeColor(turn), depth + 1);
        }
    }

    // minimax with alpha-beta pruning
    private double minimax(Node n, int depth, ChessGame.TeamColor team, double alpha, double beta) {
        if (n.getChildren().isEmpty()) return n.evaluate(team); // leaf node (evaluation function)

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
}
