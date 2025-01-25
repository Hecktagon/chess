package chess;

import java.util.HashSet;

public class MovesQueen implements Moves{
    public boolean inBounds(int num){
        return 1 <= num && num <= 8;
    }

    public HashSet<ChessMove> diagonalChecker(boolean xDirection, boolean yDirection, ChessBoard board, ChessPosition position){
        HashSet<ChessMove> diagonal = new HashSet<ChessMove>();
        int xPosition = position.getRow();
        int yPosition = position.getColumn();
        ChessPiece currentPiece = (ChessPiece) board.getPiece(position);
        ChessPosition endPosition;

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

        // checking a diagonal
        while (inBounds(xPosition) && inBounds(yPosition)) {
            endPosition = new ChessPosition(xPosition, yPosition);
            ChessPiece endPiece = (ChessPiece) board.getPiece(endPosition);

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

    public HashSet<ChessMove> straightChecker(boolean vertical, boolean direction, ChessBoard board, ChessPosition position){
        HashSet<ChessMove> straight = new HashSet<ChessMove>();
        int xPosition = position.getRow();
        int yPosition = position.getColumn();
        ChessPiece currentPiece = (ChessPiece) board.getPiece(position);
        ChessPosition endPosition;

        if (vertical) {
            xPosition = position.getRow();
            if (direction) {
                yPosition++;
            } else {
                yPosition--;
            }
        } else {
            yPosition = position.getColumn();
            if (direction) {
                xPosition++;
            } else {
                xPosition--;
            }
        }

        // checking a straight
        while (inBounds(xPosition) && inBounds(yPosition)) {
            endPosition = new ChessPosition(xPosition, yPosition);
            ChessPiece endPiece = (ChessPiece) board.getPiece(endPosition);

            if (vertical) {
                xPosition = position.getRow();
                if (direction) {
                    yPosition++;
                } else {
                    yPosition--;
                }
            } else {
                yPosition = position.getColumn();
                if (direction) {
                    xPosition++;
                } else {
                    xPosition--;
                }
            }

            // empty space, keep going
            if (endPiece == null) {
                straight.add(new ChessMove(position, endPosition, null));
            } else {
                // same team piece: stop
                if (endPiece.getTeamColor().equals(currentPiece.getTeamColor())){
                    break;
                    // enemy team piece: take, then stop.
                } else {
                    straight.add(new ChessMove(position, endPosition, null));
                    break;
                }
            }
        }
        return straight;
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

        // check + y straight
        moves.addAll(straightChecker(true, true, board, position));
        // check - y straight
        moves.addAll(straightChecker(true, false, board, position));
        // check + x straight
        moves.addAll(straightChecker(false, true, board, position));
        // check - x straight
        moves.addAll(straightChecker(false, false, board, position));

        return moves;
    }
}
