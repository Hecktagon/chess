package chess;

import java.util.HashSet;

public class MovesBishop implements Moves{
    public boolean inBounds(int num){
        return 0 <= num && num <= 7;
    }

    public HashSet<ChessMove> diagonalChecker(boolean xDirection, boolean yDirection, ChessBoard board, ChessPosition position){
        HashSet<ChessMove> diagonal = new HashSet<ChessMove>();
        int xPosition = position.getColumn() -1;
        int yPosition = position.getRow() -1;
        ChessPiece currentPiece = (ChessPiece) board.getPiece(position);
        ChessPosition endPosition;

        // checking a diagonal
        while (inBounds(xPosition) && inBounds(yPosition)) {
            // set x direction based on input
            if (xDirection){
                xPosition++;
            } else {
                xPosition--;
            }
            // set y direction based on input
            if (yDirection){
                yPosition++;
            } else {
                yPosition--;
            }

            endPosition = new ChessPosition(xPosition, yPosition);
            ChessPiece endPiece = (ChessPiece) board.getPiece(endPosition);

            // empty space, keep going
            if (endPiece == null) {
                diagonal.add(new ChessMove(position, endPosition, null));
            } else {
                // same team piece: stop
                if (endPiece.getTeamColor().equals(currentPiece.getTeamColor())){
                    break;
                    // enemy team piece: take, then stop.
                } else {
                    diagonal.add(new ChessMove(position, endPosition, null));
                    break;
                }
            }
        }
        return diagonal;
    }

    @Override
    public HashSet<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        // check positive positive diagonal
        moves.addAll(diagonalChecker(true, true, board, position));
        // check positive negative diagonal
        moves.addAll(diagonalChecker(true, false, board, position));
        // check negative positive diagonal
        moves.addAll(diagonalChecker(false, true, board, position));
        // check negative negative diagonal
        moves.addAll(diagonalChecker(false, false, board, position));
        return moves;
    }
}
