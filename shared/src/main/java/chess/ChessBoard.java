package chess;

import java.util.Arrays;
import java.util.Optional;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private Object[][] board;

    public ChessBoard() {
        board = new Object[8][8];
        initialize_board();
//        printBoard();
    }

    private void initialize_board() {
        board[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        board[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        board[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        board[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        board[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        board[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        for(int i = 0; i < 8; i++) {
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
    }

    public void printBoard() {
        System.out.println();
        System.out.println();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == null) {
                    System.out.print("0\t\t");
                }
                else {
                    if(board[i][j] instanceof ChessPiece) {
                        ChessPiece piece = (ChessPiece) board[i][j];
                        String color = piece.getTeamColor() == ChessGame.TeamColor.BLACK ? "b" : "w";
                        System.out.print(piece.getPieceType() + color + "\t");
                    }
                }
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return (ChessPiece) board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new Object[8][8];
        initialize_board();
    }

    @Override
    public boolean equals(Object obj) {
        System.out.print("\n\nCALLED ChessBoard.equals()\n\n");
        if(obj instanceof ChessBoard) {
            ChessBoard other = (ChessBoard) obj;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board[i][j] instanceof ChessPiece && other.board[i][j] instanceof ChessPiece) {
                        if (!board[i][j].equals(other.board[i][j])) {
                            return false;
                        }
                    }
                    else{
                        if (board[i][j] == null && other.board[i][j] == null) {
                            continue;
                        }
                        else if (board[i][j] == null && other.board[i][j] != null) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] instanceof ChessPiece) {
                    ChessPiece piece = (ChessPiece) board[i][j];
                    if ((j > 1) && (j % 4 == 0)) {
                        hashCode = hashCode^piece.hashCode();
                    }
                    else if (i % 2 == 0) {
                        hashCode += piece.hashCode();
                    }
                    else{
                        hashCode *= piece.hashCode();
                    }
                }
                else{
                    if ((j > 1) && (j % 4 == 0)) {
                        hashCode = hashCode^(i + j);
                    }
                    else if (i % 2 == 0) {
                        hashCode += (i * j);
                    }
                    else{
                        hashCode *= (i + 1) + (j + 1);
                    }
                }
            }
        }
        return hashCode;
    }
}
