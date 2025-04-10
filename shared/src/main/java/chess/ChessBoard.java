package chess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    public ChessPiece[][] getBoard() {
        return board;
    }

    public static String shortcutMap(ChessPiece piece){
        if (piece == null){
            return " ";
        }
        ChessPiece.PieceType type = piece.getPieceType();
        ChessGame.TeamColor color = piece.getTeamColor();
        HashMap<ChessPiece.PieceType, String> shortcuts = new HashMap<>();

        shortcuts.put(ChessPiece.PieceType.QUEEN, "Q");
        shortcuts.put(ChessPiece.PieceType.KING, "K");
        shortcuts.put(ChessPiece.PieceType.ROOK, "R");
        shortcuts.put(ChessPiece.PieceType.KNIGHT, "N");
        shortcuts.put(ChessPiece.PieceType.BISHOP, "B");
        shortcuts.put(ChessPiece.PieceType.PAWN, "P");

        String shortcut = shortcuts.get(type);

        if (color == ChessGame.TeamColor.BLACK){
            shortcut = shortcut.toLowerCase();
        }

        return shortcut;
    }

    public void printBoard(){
        System.out.print("\n");
        for(int i = 7; i >= 0; i--){
            for(int j = 0; j < 8; j++){
                ChessPiece currPiece = (ChessPiece) board[i][j];
                System.out.print("|");
                System.out.print(shortcutMap(currPiece));
            }
            System.out.print("|\n");
        }
        System.out.print("\n");
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
        return board[position.getRow()-1][position.getColumn()-1];
    }

    // returns [valid, captured]
    public boolean[] isValidMove(ChessMove move){
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        if (!start.posInBounds() || !end.posInBounds()){
            System.err.print("failed ChessBoard.isValidMove: start or end position is out of bounds.");
            return new boolean[] {false, false};
        }

        ChessPiece myPiece = getPiece(start);
        ChessGame.TeamColor myTeam = myPiece.getTeamColor();
        ChessPiece targetPiece = getPiece(end);
        ChessGame.TeamColor targetTeam;
        if (targetPiece != null) {
            targetTeam = targetPiece.getTeamColor();
            if (targetTeam != myTeam) {
                return new boolean[] {true, true};
            } else {
                return new boolean[] {false, false};
            }
        } else {
            return new boolean[] {true, false};
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        for(int i = 0; i < 8; i++){
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }

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

//        System.out.print("\nBoard Reset:\n");
//        printBoard();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
