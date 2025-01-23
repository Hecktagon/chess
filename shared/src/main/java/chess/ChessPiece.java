package chess;

import java.util.Collection;

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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("ChessPiece pieceMoves Not implemented");
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
