package chess;

import java.util.HashSet;

public class MovesPawn implements Moves {
    public boolean positionInBounds(ChessPosition pos) {
        return 0 < pos.getRow() && pos.getRow() < 9 && 0 < pos.getColumn() && pos.getColumn() < 9;
    }

    public HashSet<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        ChessPiece.PieceType[] promotions = {ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.QUEEN};

        int xPosition = position.getRow();
        int yPosition = position.getColumn();

        ChessPiece currentPiece = (ChessPiece) board.getPiece(position);
        ChessPosition endPosition;
        ChessPiece endPiece;
        int color = 1;

        if (currentPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            color = -1;
        }

        ChessPosition negative = new ChessPosition(xPosition + color, yPosition - 1);
        ChessPosition forward = new ChessPosition(xPosition + color, yPosition );
        ChessPosition positive = new ChessPosition(xPosition + color, yPosition + 1);
        ChessPosition doubleForward = new ChessPosition(xPosition + (color*2), yPosition);


        // forward negative x capture
        if (positionInBounds(negative)) {
            endPiece = (ChessPiece) board.getPiece(negative);
            if (endPiece != null && endPiece.getTeamColor() != currentPiece.getTeamColor()) {
                if (color == 1 && negative.getRow() == 8 || color == -1 && negative.getRow() == 1) {
                    for (int i = 0; i < 4; i++) {
                        moves.add(new ChessMove(position, negative, promotions[i]));
                    }
                } else {
                    moves.add(new ChessMove(position, negative, null));
                }
            }
        }

        if (positionInBounds(positive)) {
            endPiece = (ChessPiece) board.getPiece(positive);
            if (endPiece != null && endPiece.getTeamColor() != currentPiece.getTeamColor()) {
                if (color == 1 && positive.getRow() == 8 || color == -1 && positive.getRow() == 1) {
                    for (int i = 0; i < 4; i++) {
                        moves.add(new ChessMove(position, positive, promotions[i]));
                    }
                } else {
                    moves.add(new ChessMove(position, positive, null));
                }
            }
        }

        if (positionInBounds(forward)) {
            endPiece = (ChessPiece) board.getPiece(forward);
            if (endPiece == null) {
                if (color == 1 && forward.getRow() == 8 || color == -1 && forward.getRow() == 1) {
                    for (int i = 0; i < 4; i++) {
                        moves.add(new ChessMove(position, forward, promotions[i]));
                    }
                } else {
                    moves.add(new ChessMove(position, forward, null));
                }
                if (color == 1 && position.getRow() == 2 || color == -1 && position.getRow() == 7){
                    if (positionInBounds(doubleForward)){
                        if (board.getPiece(doubleForward) == null){
                            moves.add(new ChessMove(position, doubleForward, null));
                        }
                    }
                }
            }
        }


        return moves;
    }
}

