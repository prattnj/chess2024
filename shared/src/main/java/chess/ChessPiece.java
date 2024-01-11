package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor color;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition) == null) return new HashSet<>();
        if (!ChessUtil.validatePosition(myPosition)) return new HashSet<>();
        return switch (board.getPiece(myPosition).getPieceType()) {
            case KING -> kingMoves(board, myPosition);
            case QUEEN -> queenMoves(board, myPosition);
            case BISHOP -> bishopMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
        };
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                ChessPosition target = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + j);
                validateAndAdd(moves, board, new ChessMove(myPosition, target, null));
            }
        }
        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = bishopMoves(board, myPosition);
        moves.addAll(rookMoves(board, myPosition));
        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();

        // up and right
        for (int i = 1; i < 8; i++) {
            ChessPosition target = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i);
            if (!validateAndAdd(moves, board, new ChessMove(myPosition, target, null))) break;
        }

        // up and left
        for (int i = 1; i < 8; i++) {
            ChessPosition target = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i);
            if (!validateAndAdd(moves, board, new ChessMove(myPosition, target, null))) break;
        }

        // down and right
        for (int i = 1; i < 8; i++) {
            ChessPosition target = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() + i);
            if (!validateAndAdd(moves, board, new ChessMove(myPosition, target, null))) break;
        }

        // down and left
        for (int i = 1; i < 8; i++) {
            ChessPosition target = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i);
            if (!validateAndAdd(moves, board, new ChessMove(myPosition, target, null))) break;
        }

        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        Collection<ChessPosition> knightPositions = new HashSet<>();
        knightPositions.add(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1));
        knightPositions.add(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1));
        knightPositions.add(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1));
        knightPositions.add(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1));
        knightPositions.add(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2));
        knightPositions.add(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2));
        knightPositions.add(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2));
        knightPositions.add(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2));
        for (ChessPosition target : knightPositions) validateAndAdd(moves, board, new ChessMove(myPosition, target, null));
        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();

        // up
        for (int i = 1; i < 8; i++) {
            ChessPosition target = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn());
            if (!validateAndAdd(moves, board, new ChessMove(myPosition, target, null))) break;
        }

        // down
        for (int i = 1; i < 8; i++) {
            ChessPosition target = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn());
            if (!validateAndAdd(moves, board, new ChessMove(myPosition, target, null))) break;
        }

        // left
        for (int i = 1; i < 8; i++) {
            ChessPosition target = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i);
            if (!validateAndAdd(moves, board, new ChessMove(myPosition, target, null))) break;
        }

        // right
        for (int i = 1; i < 8; i++) {
            ChessPosition target = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i);
            if (!validateAndAdd(moves, board, new ChessMove(myPosition, target, null))) break;
        }

        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        int row = myPosition.getRow();
        int baseRow = color == ChessGame.TeamColor.WHITE ? 2 : 7;
        int promoRow = 9 - baseRow; // either 2 or 7, opposite of base row
        int doubleRow = color == ChessGame.TeamColor.WHITE ? 4 : 5;
        int direction = color == ChessGame.TeamColor.WHITE ? 1 : -1;

        // forward/diagonal moves
        for (int i = -1; i <= 1; i++) {
            Collection<ChessMove> toValidate = new HashSet<>();
            ChessPosition target = new ChessPosition(row + direction, myPosition.getColumn() + i);
            if (row == promoRow) {
                for (PieceType type : promoPieces()) toValidate.add(new ChessMove(myPosition, target, type));
            } else toValidate.add(new ChessMove(myPosition, target, null));
            for (ChessMove move : toValidate) validateAndAdd(moves, board, move);
        }
        // double square
        if (row == baseRow) {
            // if one square forward is empty
            if (board.getPiece(new ChessPosition(row + direction, myPosition.getColumn())) == null) {
                ChessMove doubleMove = new ChessMove(myPosition, new ChessPosition(doubleRow, myPosition.getColumn()), null);
                validateAndAdd(moves, board, doubleMove);
            }
        }

        return moves;
    }

    // return value is used for whether to continue looping (bishop, rook, queen)
    private boolean validateAndAdd(Collection<ChessMove> moves, ChessBoard board, ChessMove move) {
        boolean isPawn = board.getPiece(move.getStartPosition()).getPieceType() == PieceType.PAWN;
        if (move.getStartPosition().equals(move.getEndPosition())) return true;

        // validate positions
        if (!ChessUtil.validatePosition(move.getStartPosition()) || !ChessUtil.validatePosition(move.getEndPosition())) return false;

        ChessPiece mover = board.getPiece(move.getStartPosition());
        ChessPiece victim = board.getPiece(move.getEndPosition());
        boolean victimIsFriend = victim != null && victim.color == mover.color;
        if (mover == null) return false; // shouldn't happen
        if (victimIsFriend) return false;
        if (isPawn) {
            // diagonal
            if (move.getStartPosition().getColumn() != move.getEndPosition().getColumn()) {
                if (victim == null) return false;
            } else { // forward
                if (victim != null) return false;
            }
        }
        moves.add(move);
        return victim == null;
    }

    private Collection<PieceType> promoPieces() {
        Collection<PieceType> promos = new HashSet<>();
        promos.add(PieceType.QUEEN);
        promos.add(PieceType.ROOK);
        promos.add(PieceType.KNIGHT);
        promos.add(PieceType.BISHOP);
        return promos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece piece = (ChessPiece) o;
        return color == piece.color && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }
}
