package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // WHITE PIECES
        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        board[0][0] = new ChessPiece(white, ChessPiece.PieceType.ROOK);
        board[0][1] = new ChessPiece(white, ChessPiece.PieceType.KNIGHT);
        board[0][2] = new ChessPiece(white, ChessPiece.PieceType.BISHOP);
        board[0][3] = new ChessPiece(white, ChessPiece.PieceType.QUEEN);
        board[0][4] = new ChessPiece(white, ChessPiece.PieceType.KING);
        board[0][5] = new ChessPiece(white, ChessPiece.PieceType.BISHOP);
        board[0][6] = new ChessPiece(white, ChessPiece.PieceType.KNIGHT);
        board[0][7] = new ChessPiece(white, ChessPiece.PieceType.ROOK);
        for (int i = 0; i < 8; i++) board[1][i] = new ChessPiece(white, ChessPiece.PieceType.PAWN);

        // BLACK PIECES
        ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;
        board[7][0] = new ChessPiece(black, ChessPiece.PieceType.ROOK);
        board[7][1] = new ChessPiece(black, ChessPiece.PieceType.KNIGHT);
        board[7][2] = new ChessPiece(black, ChessPiece.PieceType.BISHOP);
        board[7][3] = new ChessPiece(black, ChessPiece.PieceType.QUEEN);
        board[7][4] = new ChessPiece(black, ChessPiece.PieceType.KING);
        board[7][5] = new ChessPiece(black, ChessPiece.PieceType.BISHOP);
        board[7][6] = new ChessPiece(black, ChessPiece.PieceType.KNIGHT);
        board[7][7] = new ChessPiece(black, ChessPiece.PieceType.ROOK);
        for (int i = 0; i < 8; i++) board[6][i] = new ChessPiece(black, ChessPiece.PieceType.PAWN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
