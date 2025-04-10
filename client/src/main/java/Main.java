import chess.*;
import ui.*;
import client.Repl;
import exception.ResponseException;
// pointless
public class Main {
    public static void main(String[] args) throws ResponseException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        // TESTING PRINTBOARD W VALID MOVES
        ChessGame testGame = new ChessGame();
        ChessPiece testQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        ChessPosition queenPosition = new ChessPosition(4, 5);
        ChessPosition testPosition = new ChessPosition(4, 5);
        testGame.getBoard().addPiece(queenPosition, testQueen);
        ChessGame.TeamColor team = (testGame.getBoard().getPiece(testPosition) == null) ? ChessGame.TeamColor.WHITE : testGame.getBoard().getPiece(testPosition).getTeamColor();

        DisplayChessBoard.printChessGame(testGame, team, testPosition);
//
//        Repl repl = new Repl(null);
//        repl.run();
    }
}