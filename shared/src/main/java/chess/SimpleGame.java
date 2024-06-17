package chess;

import util.Util;

import java.util.LinkedList;

// no castling, en passant, or other special moves
public class SimpleGame {

    private char[][] board;
    private int turn; // 1 white, 2 black
    private boolean ongoing;

    public SimpleGame() {
        resetBoard();
        turn = 1;
        ongoing = true;
    }

    public SimpleGame(ChessGame game) {
        board = new char[8][8];
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition(i, j));
                if (piece != null) board[i - 1][j - 1] = Util.getStringForPiece(piece).charAt(0);
            }
        }
        this.turn = game.getTeamTurn() == ChessGame.TeamColor.WHITE ? 1 : 2;
        this.ongoing = !game.isOver();
    }

    // example move: 0,1,3,3 == move from b1 to d4
    public LinkedList<int[]> validMoves(int team) {
        LinkedList<int[]> moves = new LinkedList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 0) continue;
                int color = Character.isLowerCase(board[i][j]) ? 2 : 1;
                if (color != team) continue;
                moves.addAll(validMoves(i, j));
            }
        }
        return moves;
    }

    public LinkedList<int[]> validMoves(int row, int col) {
        LinkedList<int[]> pieceMoves;
        LinkedList<int[]> moves = new LinkedList<>();
        if (board[row][col] == 0) return moves;
        int color = Character.isLowerCase(board[row][col]) ? 2 : 1;
        pieceMoves = switch (Util.getTypeForChar(board[row][col])) {
            case KING -> kingMoves(board, row, col);
            case QUEEN -> queenMoves(board, row, col);
            case ROOK -> rookMoves(board, row, col);
            case KNIGHT -> knightMoves(board, row, col);
            case BISHOP -> bishopMoves(board, row, col);
            case PAWN -> pawnMoves(board, row, col);
        };
        for (int[] move : pieceMoves) {
            // make move, in check?, undo move
            char mover = board[move[0]][move[1]];
            char victim = board[move[2]][move[3]];
            updateBoard(move, (char)0, mover, true);
            boolean inCheck = inCheck(color);
            updateBoard(move, mover, victim, false);
            if (!inCheck) moves.add(move);
        }
        return moves;
    }

    public void makeMove(int[] move) {
        // don't check for validity, we must be speedy
        char mover = board[move[0]][move[1]];
        updateBoard(move, (char)0, mover, true);

        turn = turn == 1 ? 2 : 1; // toggle whose turn it is

        // todo: check for checkmate/stalemate
    }

    public boolean inCheck(int team) {
        int[] pos = findKing(team);
        int row = pos[0];
        int col = pos[1];
        int d = team == 1 ? 1 : -1;
        char q = team == 1 ? 'q' : 'Q';

        // danger from pawn
        char c = team == 1 ? 'p' : 'P';
        if (positionInBounds(row + d, col - 1) && board[row + d][col - 1] == c) return true;
        if (positionInBounds(row + d, col + 1) && board[row + d][col + 1] == c) return true;

        // danger from knight
        c = team == 1 ? 'n' : 'N';
        if (positionInBounds(row + 1, col + 2) && board[row + 1][col + 2] == c) return true;
        if (positionInBounds(row + 1, col - 2) && board[row + 1][col - 2] == c) return true;
        if (positionInBounds(row - 1, col + 2) && board[row - 1][col + 2] == c) return true;
        if (positionInBounds(row - 1, col - 2) && board[row - 1][col - 2] == c) return true;
        if (positionInBounds(row + 2, col + 1) && board[row + 2][col + 1] == c) return true;
        if (positionInBounds(row + 2, col - 1) && board[row + 2][col - 1] == c) return true;
        if (positionInBounds(row - 2, col + 1) && board[row - 2][col + 1] == c) return true;
        if (positionInBounds(row - 2, col - 1) && board[row - 2][col - 1] == c) return true;

        // danger from rook/queen
        c = team == 1 ? 'r' : 'R';
        d = 1;
        while (true) {
            if (positionInBounds(row + d, col)) {
                char attacker = board[row + d][col];
                if (attacker == c || attacker == q) return true;
                else if (attacker != 0) break;
            } else break;
            d++;
        }
        d = 1;
        while (true) {
            if (positionInBounds(row - d, col)) {
                char attacker = board[row - d][col];
                if (attacker == c || attacker == q) return true;
                else if (attacker != 0) break;
            } else break;
            d++;
        }
        d = 1;
        while (true) {
            if (positionInBounds(row, col + d)) {
                char attacker = board[row][col + d];
                if (attacker == c || attacker == q) return true;
                else if (attacker != 0) break;
            } else break;
            d++;
        }
        d = 1;
        while (true) {
            if (positionInBounds(row, col - d)) {
                char attacker = board[row][col - d];
                if (attacker == c || attacker == q) return true;
                else if (attacker != 0) break;
            } else break;
            d++;
        }

        // danger from bishop/queen
        c = team == 1 ? 'b' : 'B';
        d = 1;
        while (true) {
            if (positionInBounds(row + d, col + d)) {
                char attacker = board[row + d][col + d];
                if (attacker == c || attacker == q) return true;
                else if (attacker != 0) break;
            } else break;
            d++;
        }
        d = 1;
        while (true) {
            if (positionInBounds(row + d, col - d)) {
                char attacker = board[row + d][col - d];
                if (attacker == c || attacker == q) return true;
                else if (attacker != 0) break;
            } else break;
            d++;
        }
        d = 1;
        while (true) {
            if (positionInBounds(row - d, col + d)) {
                char attacker = board[row - d][col + d];
                if (attacker == c || attacker == q) return true;
                else if (attacker != 0) break;
            } else break;
            d++;
        }
        d = 1;
        while (true) {
            if (positionInBounds(row - d, col - d)) {
                char attacker = board[row - d][col - d];
                if (attacker == c || attacker == q) return true;
                else if (attacker != 0) break;
            } else break;
            d++;
        }

        return false;
    }

    public int countInstances(char c) {
        int counter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == c) counter++;
            }
        }
        return counter;
    }

    public char[][] getBoard() {
        return board;
    }

    public int getTurn() {
        return turn;
    }

    public boolean isOngoing() {
        return ongoing;
    }

    public SimpleGame clone() {
        SimpleGame clone = new SimpleGame();
        for (int i = 0; i < 8; i++) System.arraycopy(board[i], 0, clone.board[i], 0, 8);
        clone.turn = turn;
        clone.ongoing = ongoing;
        return clone;
    }

    private void resetBoard() {
        board = new char[8][8];

        board[0][0] = 'R';
        board[0][1] = 'N';
        board[0][2] = 'B';
        board[0][3] = 'Q';
        board[0][4] = 'K';
        board[0][5] = 'B';
        board[0][6] = 'N';
        board[0][7] = 'R';

        for (int i = 0; i < 8; i++) {
            board[1][i] = 'P';
            board[6][i] = 'p';
        }

        board[7][0] = 'r';
        board[7][1] = 'n';
        board[7][2] = 'b';
        board[7][3] = 'q';
        board[7][4] = 'k';
        board[7][5] = 'b';
        board[7][6] = 'n';
        board[7][7] = 'r';
    }

    private void updateBoard(int[] positions, char piece1, char piece2, boolean checkPromo) {
        board[positions[0]][positions[1]] = piece1;
        board[positions[2]][positions[3]] = piece2;

        // special case: pawn promotion
        if (positions.length > 4 && checkPromo) {
            char promo = Util.getCharForType(ChessPiece.PieceType.values()[positions[4]]);
            if (Character.isLowerCase(board[positions[2]][positions[3]])) promo = Character.toLowerCase(promo);
            board[positions[2]][positions[3]] = promo;
        }
    }

    public static LinkedList<int[]> kingMoves(char[][] board, int row, int col) {
        LinkedList<int[]> moves = new LinkedList<>();
        int team = Character.isLowerCase(board[row][col]) ? 2 : 1;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (validatePosition(board, newRow, newCol, team)) moves.add(new int[]{row, col, newRow, newCol});
            }
        }
        return moves;
    }

    public static LinkedList<int[]> queenMoves(char[][] board, int row, int col) {
        int team = Character.isLowerCase(board[row][col]) ? 2 : 1;
        LinkedList<int[]> moves = rookMoves(board, row, col);
        moves.addAll(bishopMoves(board, row, col));
        return moves;
    }

    public static LinkedList<int[]> rookMoves(char[][] board, int row, int col) {
        LinkedList<int[]> moves = new LinkedList<>();
        int team = Character.isLowerCase(board[row][col]) ? 2 : 1;

        int d = 1;
        while(true) {
            int newRow = row + d;
            if (validatePosition(board, newRow, col, team)) {
                moves.add(new int[]{row, col, newRow, col});
                if (board[newRow][col] != 0) break;
            }
            else break;
            d++;
        }

        d = 1;
        while(true) {
            int newRow = row - d;
            if (validatePosition(board, newRow, col, team)) {
                moves.add(new int[]{row, col, newRow, col});
                if (board[newRow][col] != 0) break;
            }
            else break;
            d++;
        }

        d = 1;
        while(true) {
            int newCol = col + d;
            if (validatePosition(board, row, newCol, team)) {
                moves.add(new int[]{row, col, row, newCol});
                if (board[row][newCol] != 0) break;
            }
            else break;
            d++;
        }

        d = 1;
        while(true) {
            int newCol = col - d;
            if (validatePosition(board, row, newCol, team)) {
                moves.add(new int[]{row, col, row, newCol});
                if (board[row][newCol] != 0) break;
            }
            else break;
            d++;
        }

        return moves;
    }

    public static LinkedList<int[]> knightMoves(char[][] board, int row, int col) {
        LinkedList<int[]> moves = new LinkedList<>();
        int team = Character.isLowerCase(board[row][col]) ? 2 : 1;

        int newRow = row + 1;
        int newCol = col + 2;
        if (validatePosition(board, newRow, newCol, team)) moves.add(new int[]{row, col, newRow, newCol});

        newRow = row + 2;
        newCol = col + 1;
        if (validatePosition(board, newRow, newCol, team)) moves.add(new int[]{row, col, newRow, newCol});

        newRow = row + 1;
        newCol = col - 2;
        if (validatePosition(board, newRow, newCol, team)) moves.add(new int[]{row, col, newRow, newCol});

        newRow = row + 2;
        newCol = col - 1;
        if (validatePosition(board, newRow, newCol, team)) moves.add(new int[]{row, col, newRow, newCol});

        newRow = row - 1;
        newCol = col + 2;
        if (validatePosition(board, newRow, newCol, team)) moves.add(new int[]{row, col, newRow, newCol});

        newRow = row - 2;
        newCol = col + 1;
        if (validatePosition(board, newRow, newCol, team)) moves.add(new int[]{row, col, newRow, newCol});

        newRow = row - 1;
        newCol = col - 2;
        if (validatePosition(board, newRow, newCol, team)) moves.add(new int[]{row, col, newRow, newCol});

        newRow = row - 2;
        newCol = col - 1;
        if (validatePosition(board, newRow, newCol, team)) moves.add(new int[]{row, col, newRow, newCol});

        return moves;
    }

    public static LinkedList<int[]> bishopMoves(char[][] board, int row, int col) {
        LinkedList<int[]> moves = new LinkedList<>();
        int team = Character.isLowerCase(board[row][col]) ? 2 : 1;

        int d = 1;
        while(true) {
            int newRow = row + d;
            int newCol = col + d;
            if (validatePosition(board, newRow, newCol, team)) {
                moves.add(new int[]{row, col, newRow, newCol});
                if (board[newRow][newCol] != 0) break;
            }
            else break;
            d++;
        }

        d = 1;
        while(true) {
            int newRow = row + d;
            int newCol = col - d;
            if (validatePosition(board, newRow, newCol, team)) {
                moves.add(new int[]{row, col, newRow, newCol});
                if (board[newRow][newCol] != 0) break;
            }
            else break;
            d++;
        }

        d = 1;
        while(true) {
            int newRow = row - d;
            int newCol = col + d;
            if (validatePosition(board, newRow, newCol, team)) {
                moves.add(new int[]{row, col, newRow, newCol});
                if (board[newRow][newCol] != 0) break;
            }
            else break;
            d++;
        }

        d = 1;
        while(true) {
            int newRow = row - d;
            int newCol = col - d;
            if (validatePosition(board, newRow, newCol, team)) {
                moves.add(new int[]{row, col, newRow, newCol});
                if (board[newRow][newCol] != 0) break;
            }
            else break;
            d++;
        }

        return moves;
    }

    public static LinkedList<int[]> pawnMoves(char[][] board, int row, int col) {
        LinkedList<int[]> moves = new LinkedList<>();
        int team = Character.isLowerCase(board[row][col]) ? 2 : 1;

        int doubleRow = team == 1 ? 1 : 6;
        int promoRow = 7 - doubleRow;
        int d = team == 1 ? 1 : -1;
        int newRow = row + d;

        // forward 1, diagonals
        if (row != promoRow)
            pawnHelper(board, moves, row, col, team, false);

        // promos
        if (row == promoRow)
            pawnHelper(board, moves, row, col, team, true);

        // forward 2
        if (row == doubleRow) {
            int newNewRow = row + d + d;
            if (board[newRow][col] == 0 && board[newNewRow][col] == 0) moves.add(new int[]{row, col, newNewRow, col});
        }

        return moves;
    }

    private static void pawnHelper(char[][] board, LinkedList<int[]> moves, int row, int col, int team, boolean promo) {
        int d = team == 1 ? 1 : -1;
        int newRow = row + d;
        ChessPiece.PieceType[] promos = new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP};

        // forward 1
        if (board[newRow][col] == 0) {
            if (promo) for (ChessPiece.PieceType type : promos) moves.add(new int[]{row, col, newRow, col, type.ordinal()});
            else moves.add(new int[]{row, col, newRow, col});
        }

        // diagonal -
        if (positionInBounds(newRow, col - 1)) {
            char victim = board[newRow][col - 1];
            if (victim != 0) {
                int victimColor = Character.isLowerCase(victim) ? 2 : 1;
                if (victimColor != team) {
                    if (promo) for (ChessPiece.PieceType type : promos) moves.add(new int[]{row, col, newRow, col - 1, type.ordinal()});
                    else moves.add(new int[]{row, col, newRow, col - 1});
                }
            }
        }

        // diagonal +
        if (positionInBounds(newRow, col + 1)) {
            char victim = board[newRow][col + 1];
            if (victim != 0) {
                int victimColor = Character.isLowerCase(victim) ? 2 : 1;
                if (victimColor != team) {
                    if (promo) for (ChessPiece.PieceType type : promos) moves.add(new int[]{row, col, newRow, col + 1, type.ordinal()});
                    else moves.add(new int[]{row, col, newRow, col + 1});
                }
            }
        }
    }

    // see if a piece can move to the location specified (not applicable to pawns)
    private static boolean validatePosition(char[][] board, int row, int col, int moverTeam) {
        if (!positionInBounds(row, col)) return false;
        if (board[row][col] == 0) return true;
        int victimTeam = Character.isLowerCase(board[row][col]) ? 2 : 1;
        return moverTeam != victimTeam;
    }

    private static boolean positionInBounds(int row, int col) {
        return row >= 0 && row <= 7 && col >= 0 && col <= 7;
    }

    private int[] findKing(int team) {
        char c = team == 1 ? 'K' : 'k';
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == c) return new int[]{i, j};
            }
        }
        return null;
    }
}
