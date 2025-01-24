package chess;

import java.util.HashSet;

public class MovesKing implements Moves{
    public boolean inBounds(int num) {
        return 1 <= num && num <= 8;
    }

    public HashSet<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        int xPosition = position.getRow();
        int yPosition = position.getColumn();
        int newX;
        int newY;

        ChessPiece currentPiece = (ChessPiece) board.getPiece(position);
        ChessPosition endPosition;

        for (int i = -1; i < 2; i++) {
            newX = xPosition + i;
            for (int j = -1; j < 2; j++) {
                newY = yPosition + j;
                if (inBounds(newX) && inBounds(newY)) {
                    endPosition = new ChessPosition(newX, newY);
                    ChessPiece endPiece = (ChessPiece) board.getPiece(endPosition);

                    if (endPiece == null) {
                        moves.add(new ChessMove(position, endPosition, null));
                    } else {
                        // same team piece: stop
                        if (endPiece.getTeamColor().equals(currentPiece.getTeamColor())) {
                            continue;
                            // enemy team piece: take, then stop.
                        } else {
                            moves.add(new ChessMove(position, endPosition, null));

                        }
                    }
                }
            }
        }
        return moves;
    }
}
