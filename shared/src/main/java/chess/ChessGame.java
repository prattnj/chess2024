package chess;

import util.Util;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;
    private final List<ChessMove> moveHistory = new ArrayList<>();
    private boolean isOver = false;
    private final Set<ChessMove> enPassantMoves = new HashSet<>();

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> moves = new HashSet<>();
        if (piece == null) return moves;
        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);

        // filter out moves that put the king in danger
        for (ChessMove move : allMoves) {
            ChessBoard boardCopy = new ChessBoard(board);
            boardCopy.addPiece(move.getEndPosition(), piece);
            boardCopy.addPiece(move.getStartPosition(), null);
            if (!positionIsEndangered(boardCopy, findKing(boardCopy, piece.getTeamColor()))) moves.add(move);
        }

        // add castling / en passant
        if (piece.getPieceType() == ChessPiece.PieceType.KING) moves.addAll(addCastling(piece));
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) moves.addAll(addEnPassant(piece));

        return moves;
    }

    /**
     * Gets a collection of every move a player can make (uses the other validMoves method)
     *
     * @param color the player in question
     * @return A collection of valid moves
     */
    public Collection<ChessMove> validMoves(TeamColor color) {
        Collection<ChessMove> moves = new HashSet<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null || piece.getTeamColor() != color) continue;
                moves.addAll(validMoves(pos));
            }
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // make sure game is ongoing
        if (isOver) throw new InvalidMoveException("Error: game is over");

        // make sure move contains valid positions
        if (!ChessUtil.validatePosition(move.getStartPosition()) || !ChessUtil.validatePosition(move.getEndPosition())){
            throw new InvalidMoveException("Error: malformed move");
        }

        // make sure move is valid
        ChessPiece mover = board.getPiece(move.getStartPosition());
        if (mover == null) throw new InvalidMoveException("Error: empty square");
        if (mover.getTeamColor() != teamTurn) throw new InvalidMoveException("Error: it is not your turn");

        if (!validMoves(move.getStartPosition()).contains(move)) throw new InvalidMoveException("Error: invalid move");

        // at this point, move is valid and can be executed
        board.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(mover.getTeamColor(), move.getPromotionPiece()));
        } else board.addPiece(move.getEndPosition(), mover);

        // special case for castling: also move the rook
        if (mover.getPieceType() == ChessPiece.PieceType.KING) {
            int diff = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();
            int row = move.getStartPosition().getRow();
            if (diff == 2) {
                // this move is a valid right castle
                board.addPiece(new ChessPosition(row, 6), board.getPiece(new ChessPosition(row, 8)));
                board.addPiece(new ChessPosition(row, 8), null);
            } else if (diff == -2) {
                // this move is a valid left castle
                board.addPiece(new ChessPosition(row, 4), board.getPiece(new ChessPosition(row, 1)));
                board.addPiece(new ChessPosition(row, 1), null);
            }
        }

        // special case for en passant: also remove the captured pawn
        if (enPassantMoves.contains(move)) {
            int mod = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();
            board.addPiece(new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getColumn() + mod), null);
        }

        moveHistory.add(move);

        // determine whether this move ended the game
        TeamColor opponent = Util.oppositeColor(teamTurn);
        if (isInCheckmate(opponent) || isInStalemate(opponent)) isOver = true;

        // update whose turn it is
        toggleTeamTurn();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return positionIsEndangered(board, findKing(board, teamColor));
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return validMoves(teamColor).isEmpty() && isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return validMoves(teamColor).isEmpty() && !isInCheck(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        isOver = false;
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public boolean isOver() {
        return isOver;
    }

    public void setIsOver(boolean isOver) {
        this.isOver = isOver;
    }

    public ChessGame clone() {
        ChessGame clone = new ChessGame();
        clone.board = new ChessBoard(board);
        clone.isOver = isOver;
        clone.teamTurn = teamTurn;
        clone.moveHistory.addAll(moveHistory);
        clone.enPassantMoves.addAll(enPassantMoves);
        return clone;
    }

    private void toggleTeamTurn() {
        teamTurn = Util.oppositeColor(teamTurn);
    }

    private ChessPosition findKing(ChessBoard board, ChessGame.TeamColor color) {

        // this method assumes that each team has 0-1 kings on the board
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color) return pos;
            }
        }
        return null;
    }

    private boolean positionIsEndangered(ChessBoard board, ChessPosition endPos) {

        // checks if a position is endangered by any enemy piece
        if (endPos == null) return false;
        ChessPiece victim = board.getPiece(endPos);
        if (victim == null) return false;

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition startPos = new ChessPosition(i, j);
                if (startPos.equals(endPos)) continue;

                ChessPiece attacker = board.getPiece(startPos);
                if (attacker == null || attacker.getTeamColor() == victim.getTeamColor()) continue;
                Collection<ChessMove> moves = attacker.pieceMoves(board, startPos);
                if (moves == null) return false;
                if (moves.contains(new ChessMove(startPos, endPos, null)) ||
                        moves.contains(new ChessMove(startPos, endPos, ChessPiece.PieceType.ROOK)) ||
                        moves.contains(new ChessMove(startPos, endPos, ChessPiece.PieceType.KNIGHT)) ||
                        moves.contains(new ChessMove(startPos, endPos, ChessPiece.PieceType.BISHOP)) ||
                        moves.contains(new ChessMove(startPos, endPos, ChessPiece.PieceType.QUEEN))) {
                    return true;
                }
            }
        }

        return false;
    }

    private Collection<ChessMove> addCastling(ChessPiece king) {

        Collection<ChessMove> moves = new HashSet<>();
        TeamColor color = king.getTeamColor();

        // CASTLING CONDITIONS:
        // Condition 0: King (0.1) and Rook(s) (0.2) are in the correct position (for abnormal test cases)
        // Condition 1: King (1.1) and Rook(s) (1.2) have not moved
        // Condition 2: There are no pieces between King and Rook(s)
        // Condition 3: The King is not in check
        // Condition 4: Neither piece is in danger after the move

        int row = color == TeamColor.WHITE ? 1 : 8;
        ChessPosition kingPos = findPiece(board, king);
        if (kingPos == null) return moves; // This should never be the case

        ChessPiece leftRook = board.getPiece(new ChessPosition(row, 1));
        ChessPiece rightRook = board.getPiece(new ChessPosition(row, 8));

        // Condition 0.1
        if (!kingPos.equals(new ChessPosition(row, 5))) return moves;

        // Condition 1.1
        if (pieceHasMoved(king)) return moves;

        // Condition 3
        if (isInCheck(color)) return moves;

        // CASTLE LEFT

        // Condition 0.2
        if (leftRook != null && leftRook.getPieceType() == ChessPiece.PieceType.ROOK && leftRook.getTeamColor() == color) {
            // Condition 1.2
            if (!pieceHasMoved(leftRook)) {
                // Condition 2
                boolean condition2 = true;
                for (int i = 2; i <= 4; i++) if (board.getPiece(new ChessPosition(row, i)) != null) condition2 = false;
                if (condition2) {
                    // Condition 4: create copy of board and execute move
                    ChessPosition newKingPos = new ChessPosition(row, 3);
                    ChessPosition newRookPos = new ChessPosition(row, 4);
                    ChessMove move = new ChessMove(kingPos, newKingPos, null);
                    ChessBoard boardCopy = new ChessBoard(board);
                    // Squares to update:
                    // New king square (row, 3) -> king
                    // Old king square (row, 5) -> null
                    // New rook square (row, 4) -> rook
                    // Old rook square (row, 1) -> null
                    boardCopy.addPiece(move.getEndPosition(), king);
                    boardCopy.addPiece(move.getStartPosition(), null);
                    boardCopy.addPiece(newRookPos, leftRook);
                    boardCopy.addPiece(new ChessPosition(row, 1), null);
                    if (!positionIsEndangered(boardCopy, newKingPos) && !positionIsEndangered(boardCopy, newRookPos)) moves.add(move);
                }
            }
        }

        // CASTLE RIGHT

        // Condition 0.2
        if (rightRook != null && rightRook.getPieceType() == ChessPiece.PieceType.ROOK && rightRook.getTeamColor() == color) {
            // Condition 1.2
            if (!pieceHasMoved(rightRook)) {
                // Condition 2
                boolean condition2 = true;
                for (int i = 6; i <= 7; i++) if (board.getPiece(new ChessPosition(row, i)) != null) condition2 = false;
                if (condition2) {
                    // Condition 4: create copy of board and execute move
                    ChessPosition newKingPos = new ChessPosition(row, 7);
                    ChessPosition newRookPos = new ChessPosition(row, 6);
                    ChessMove move = new ChessMove(kingPos, newKingPos, null);
                    ChessBoard boardCopy = new ChessBoard(board);
                    // Squares to update:
                    // New king square (row, 7) -> king
                    // Old king square (row, 5) -> null
                    // New rook square (row, 6) -> rook
                    // Old rook square (row, 8) -> null
                    boardCopy.addPiece(move.getEndPosition(), king);
                    boardCopy.addPiece(move.getStartPosition(), null);
                    boardCopy.addPiece(newRookPos, leftRook);
                    boardCopy.addPiece(new ChessPosition(row, 8), null);
                    if (!positionIsEndangered(boardCopy, newKingPos) && !positionIsEndangered(boardCopy, newRookPos)) moves.add(move);
                }
            }
        }

        return moves;
    }

    private Collection<ChessMove> addEnPassant(ChessPiece pawn) {

        Collection<ChessMove> moves = new HashSet<>();

        ChessPosition startPos = findPiece(board, pawn);
        if (startPos == null) return moves;
        TeamColor color = pawn.getTeamColor();

        // Make sure the last move was a double pawn move
        if (moveHistory.isEmpty()) return moves;
        ChessMove lastMove = moveHistory.getLast();
        if (!Objects.equals(lastMove.getEndPosition(), new ChessPosition(startPos.getRow(), startPos.getColumn() - 1)) &&
                !Objects.equals(lastMove.getEndPosition(), new ChessPosition(startPos.getRow(), startPos.getColumn() + 1))) {
            return moves;
        }
        if (Math.abs(lastMove.getEndPosition().getRow() - lastMove.getStartPosition().getRow()) != 2) return moves;
        ChessPiece enemy = board.getPiece(lastMove.getEndPosition());
        if (enemy == null || enemy.getPieceType() != ChessPiece.PieceType.PAWN || enemy.getTeamColor() == color) return moves;

        int rowMod = color == TeamColor.WHITE ? 1 : -1;
        moves.add(new ChessMove(startPos, new ChessPosition(startPos.getRow() + rowMod, lastMove.getEndPosition().getColumn()), null));

        enPassantMoves.addAll(moves);
        return moves;
    }

    // scans moves history to see if this piece has moved
    private boolean pieceHasMoved(ChessPiece piece) {
        ChessPosition pos = findPiece(board, piece);
        for (ChessMove move : moveHistory) if (move.getEndPosition().equals(pos)) return true;
        return false;
    }

    // iterates through board to find piece
    private ChessPosition findPiece(ChessBoard board, ChessPiece piece) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                if (board.getPiece(pos) == piece) return pos;
            }
        }
        return null;
    }
}
