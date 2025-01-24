package chess;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;


/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor teamColor;
    private ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
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
        return this.teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.pieceType;
    }

    private Moves getPieceMoves(PieceType pieceType) {
        HashMap<PieceType, Moves> movesMap = new HashMap<>();
//        movesMap.put(PieceType.KING, new MovesKing());
//        movesMap.put(PieceType.QUEEN, new MovesQueen());
        movesMap.put(PieceType.BISHOP, new MovesBishop());
//        movesMap.put(PieceType.KNIGHT, new MovesKnight());
//        movesMap.put(PieceType.ROOK, new MovesRook());
//        movesMap.put(PieceType.PAWN, new MovesPawn());

        return movesMap.get(pieceType);
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        System.out.print("\n\nCALLED ChessPiece.pieceMoves()\n\n");
        ChessPiece myPiece = (ChessPiece) board.getPiece(myPosition);
        if (myPiece == null) {
            return null;
        } else {
            Moves movesClass = getPieceMoves(myPiece.getPieceType());
            return movesClass.pieceMoves(board, myPosition);
        }
    }

    @Override
    public boolean equals(Object obj) {
        System.out.print("\n\nCALLED ChessPiece.equals()\n\n");
        if (obj instanceof ChessPiece) {
            ChessPiece otherPiece = (ChessPiece) obj;
            return this.teamColor == otherPiece.teamColor && this.pieceType == otherPiece.pieceType;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int colorCode = this.teamColor == ChessGame.TeamColor.WHITE ? 1 : 0;
        int typeCode = this.pieceType.ordinal();
        return colorCode + typeCode;
    }
}
