package service.ai;

import chess.*;
import model.AILevel;
import util.Util;

import java.util.*;

public class ChessAI {

    private int totalNodes;
    private final int maxDepth;
    private ChessGame.TeamColor maxPlayer;

    public ChessAI(AILevel level) {
        this.maxDepth = switch (level) {
            case EASY -> 2;
            case MEDIUM -> 4;
            case HARD -> 6;
        };
    }

    public ChessMove getMove(ChessGame game, ChessGame.TeamColor turn) {
        if (game.getTeamTurn() != turn) return null; // not the AI's turn
        maxPlayer = turn;
        totalNodes = 0;

        SimpleGame sg = new SimpleGame(game);

        double bestValue = Double.NEGATIVE_INFINITY;
        List<int[]> bestMoves = new ArrayList<>();

        for (int[] m : sg.validMoves(turn == ChessGame.TeamColor.WHITE ? 1 : 2)) {
            SimpleGame sg1 = new SimpleGame(game);
            sg1.makeMove(m);
            Node child = new Node(sg1);
            totalNodes++;
            double value = minimax(child, 1, Util.oppositeColor(turn), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            if (value > bestValue) {
                bestMoves.clear();
                bestMoves.add(m);
                bestValue = value;
            } else if (value == bestValue) {
                bestMoves.add(m);
            }
        }

        // select a random move of the best/worst moves
        return simpleToComplexMove(bestMoves.get(new Random().nextInt(bestMoves.size())));
    }

    // minimax with alpha-beta pruning
    private double minimax(Node n, int depth, ChessGame.TeamColor team, double alpha, double beta) {
        if (depth == maxDepth) return n.evaluate(team); // leaf node (evaluation function)

        generateChildren(n, team);

        if (team == maxPlayer) {
            double max = Double.NEGATIVE_INFINITY;
            for (Node child : n.getChildren()) {
                double value = minimax(child, depth + 1, Util.oppositeColor(maxPlayer), alpha, beta);
                if (value > max) max = value;
                if (max > alpha) alpha = max;
                if (beta <= alpha) break;
            }
            return max;
        } else {
            double min = Double.POSITIVE_INFINITY;
            for (Node child : n.getChildren()) {
                double value = minimax(child, depth + 1, maxPlayer, alpha, beta);
                if (value < min) min = value;
                if (min < beta) beta = min;
                if (beta <= alpha) break;
            }
            return min;
        }
    }

    private void generateChildren(Node n, ChessGame.TeamColor turn) {
        LinkedList<int[]> moves = n.getGame().validMoves(turn == ChessGame.TeamColor.WHITE ? 1 : 2);
        for (int[] m : moves) {
            SimpleGame game = n.getGame().clone();
            game.makeMove(m);
            Node child = new Node(game);
            n.addChild(child);
            totalNodes++;
        }
    }

    private ChessMove simpleToComplexMove(int[] m) {
        ChessPosition start = new ChessPosition(m[0] + 1, m[1] + 1);
        ChessPosition end = new ChessPosition(m[2] + 1, m[3] + 1);
        ChessPiece.PieceType promo = null;
        if (m.length > 4) promo = ChessPiece.PieceType.values()[m[4]];
        return new ChessMove(start, end, promo);
    }
}
