package chess;

import java.util.Arrays;
import java.util.HashSet;

public class MovesPawn implements Moves{
    public int[][] pieceMoves(ChessBoard board, ChessPosition myPos){
        HashSet<int[]> finalDirections = new HashSet<>();

        ChessPiece myPiece = board.getPiece(myPos);
        int myY = myPos.getRow();
        int myX = myPos.getColumn();
        ChessGame.TeamColor myColor = myPiece.getTeamColor();
        int colorDirection = myColor == ChessGame.TeamColor.WHITE ? 1 : -1;
        int canMove2 = (colorDirection == 1 && myY == 2) || (colorDirection == -1 && myY == 7) ? 1 : 0;
        int promotes = (colorDirection == 1 && myY == 7) || (colorDirection == -1 && myY == 2) ? 1 : 0;


        ChessPosition frontNegativePos = new ChessPosition(myY + colorDirection, myX - 1);
        boolean pieceFrontNegative = frontNegativePos.posInBounds() && board.getPiece(frontNegativePos) != null;

        ChessPosition frontPositivePos = new ChessPosition(myY + colorDirection, myX + 1);
        boolean pieceFrontPositive = frontPositivePos.posInBounds() && board.getPiece(frontPositivePos) != null;

        boolean pieceFront;
        ChessPosition frontPos = new ChessPosition(myY + colorDirection, myX);
        if (frontPos.posInBounds()) {
            pieceFront = board.getPiece(frontPos) != null;
        } else {
            pieceFront = true;
        }

        int[] frontMove2 = {colorDirection, 0, 1 + canMove2, promotes};
        int[] front = {colorDirection, 0, 1, promotes};
        int[] frontNegative = {colorDirection, -1, 1, promotes};
        int[] frontPositive = {colorDirection, 1, 1, promotes};


        /* if the space in front of pawn is empty, it checks if pawn is in position to move 2 spaces,
        and if two spaces ahead isn't occupied, pawn can move 2 ahead, otherwise, pawn can only move 1. */
        if (!pieceFront){
            if (canMove2 == 1 && board.getPiece(new ChessPosition(myY + colorDirection*2, myX)) == null){
                finalDirections.add(frontMove2);
            } else {
                finalDirections.add(front);
            }
        }

        if (pieceFrontNegative){
            finalDirections.add(frontNegative);
        }

        if (pieceFrontPositive){
            finalDirections.add(frontPositive);
        }

        // toArray takes a parameter to allow you to specify its return type. Otherwise, it defaults to Object[].
        return finalDirections.toArray(new int[finalDirections.size()][]);
    }
}
