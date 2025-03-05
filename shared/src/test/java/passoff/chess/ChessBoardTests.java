package passoff.chess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ChessBoardTests {

    @Test
    @DisplayName("Add and Get Piece")
    public void getAddPiece() {
        ChessPosition position = new ChessPosition(4, 4);
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);

        var board = new ChessBoard();

        System.out.print("\n\n[Before Add]\n\n");

        board.addPiece(position, piece);

        System.out.print("\n\n[After Add]\n\n");

        ChessPiece foundPiece = board.getPiece(position);

        String stringPiece = piece.toString();
        String stringFoundPiece = foundPiece.toString();

        Assertions.assertEquals(piece.getPieceType(), foundPiece.getPieceType(),
                "ChessPiece " + stringFoundPiece + " returned by getPiece had the wrong piece type. Expected: " + stringPiece);
        Assertions.assertEquals(piece.getTeamColor(), foundPiece.getTeamColor(),
                "ChessPiece " + stringFoundPiece + " returned by getPiece had the wrong team color. Expected: " + stringPiece);
    }


    @Test
    @DisplayName("Reset Board")
    public void defaultGameBoard() {
        var expectedBoard = TestUtilities.defaultBoard();

        System.out.print("\n\n[Expected:]\n\n");

        expectedBoard.printBoard();

        var actualBoard = new ChessBoard();
        actualBoard.resetBoard();

        System.out.print("\n\n[Actual:]\n\n");

        actualBoard.printBoard();

        Assertions.assertEquals(expectedBoard, actualBoard);
    }


    @Test
    @DisplayName("Piece Move on All Pieces")
    public void pieceMoveAllPieces() {
        var board = new ChessBoard();
        board.resetBoard();
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if(piece != null) {
                    Assertions.assertDoesNotThrow(() -> piece.pieceMoves(board, position));
                }
            }
        }
    }

}