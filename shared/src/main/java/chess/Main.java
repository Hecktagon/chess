package chess;

import java.util.Collection;
import java.util.HashSet;

public class Main {
    public static void main(String []args) {
        ChessBoard myBoard = new ChessBoard();
        System.out.print("\n\nINITIAL BOARD:\n");
        myBoard.resetBoard();
        myBoard.printBoard();

        ChessPosition bishopPos = new ChessPosition(2,7);
        System.out.print("\nMADE BISHOP POSITION\n");
        ChessPiece bishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        System.out.print("\nMADE BISHOP\n");
        myBoard.addPiece(bishopPos,bishop);
        System.out.print("\nADDED BISHOP:\n");
        Collection<ChessMove> bishopMoves = bishop.pieceMoves(myBoard, bishopPos);
        System.out.print("\nMADE BISHOP MOVES\n");
//        bishop.printMoves(myBoard, bishopPos, bishopMoves);


    }
}
