package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    ChessPosition start;
    ChessPosition end;
    ChessPiece.PieceType promotion;
    boolean castle;
    boolean enPassant;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        start = startPosition;
        end = endPosition;
        promotion = promotionPiece;
        castle = false;
        enPassant = false;
    }

    public boolean isCastle() {
        return castle;
    }

    public void setCastle(boolean yesNo) {
        castle = yesNo;
    }

    public boolean isEnPassant() {
        return enPassant;
    }

    public void setEnPassant(boolean yesNo) {
        enPassant = yesNo;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(start, chessMove.start) && Objects.equals(end, chessMove.end) && promotion == chessMove.promotion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promotion);
    }

    @Override
    public String toString() {
        return "ChessMove{" + "\n" +
                "start= " + "row: " + start.getRow() + " col: " + start.getColumn() + "\n" +
                ", end= " + "row: " + end.getRow() + " col: " + end.getColumn() + "\n" +
                ", promotion=" + promotion + "\n" +
                ", castle=" + castle + "\n" +
                ", enPassant=" + enPassant + "\n" +
                '}';
    }
}
