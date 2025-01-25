package chess;

import java.util.HashSet;

public class MovesKnight implements Moves {
    public boolean positionInBounds(ChessPosition pos) {
        return 0 < pos.getRow() && pos.getRow() < 9 && 0 < pos.getColumn() && pos.getColumn() < 9;
    }

    public HashSet<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        int xPosition = position.getRow();
        int yPosition = position.getColumn();

        ChessPiece currentPiece = (ChessPiece) board.getPiece(position);
        ChessPosition endPosition;

        ChessPosition[] movesArray = {
                new ChessPosition(xPosition + 1, yPosition + 2),
                new ChessPosition(xPosition + 1, yPosition - 2),
                new ChessPosition(xPosition - 1, yPosition + 2),
                new ChessPosition(xPosition - 1, yPosition - 2),
                new ChessPosition(xPosition + 2, yPosition + 1),
                new ChessPosition(xPosition + 2, yPosition - 1),
                new ChessPosition(xPosition - 2, yPosition + 1),
                new ChessPosition(xPosition - 2, yPosition - 1)
        };

        for (int i = 0; i < 8; i++) {
            endPosition = movesArray[i];

            if (positionInBounds(endPosition)) {
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

        return moves;
    }
}
