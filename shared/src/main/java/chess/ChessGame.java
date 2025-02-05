package chess;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor turn;
    ChessBoard myBoard;

    public ChessGame() {
        turn = TeamColor.WHITE;
        myBoard = new ChessBoard();
        myBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    public void flipTeamTurn(){
        if (turn == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece myPiece = myBoard.getPiece(startPosition);
        if (myPiece == null){
            return null;
        }

        Collection<ChessMove> allMoves = myPiece.pieceMoves(myBoard, startPosition);

        allMoves.removeIf(this::endangersKing);

        return allMoves;
    }

    public boolean endangersKing(ChessMove move){
        ChessPosition start = move.getStartPosition();
        ChessPiece myPiece = myBoard.getPiece(start);
        ChessPosition end = move.getEndPosition();
        ChessPiece endPiece = myBoard.getPiece(end);

        myBoard.addPiece(start, null);
        myBoard.addPiece(end, myPiece);

        boolean causesCheck = isInCheck(myPiece.getTeamColor());

        myBoard.addPiece(start, myPiece);
        myBoard.addPiece(end, endPiece);

        return causesCheck;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        Collection<ChessMove> valids = validMoves(start);

        if (valids != null && valids.contains(move) && start.posInBounds() && end.posInBounds()){
            ChessPiece myPiece = myBoard.getPiece(start);
            if(myPiece != null && myPiece.getTeamColor() == turn) {
                myBoard.addPiece(start, null);
                if (move.getPromotionPiece() == null){
                    myBoard.addPiece(end, myPiece);
                } else {
                    myBoard.addPiece(end, new ChessPiece(myPiece.getTeamColor(), move.getPromotionPiece()));
                }
                System.out.printf("\n%s Moved:\n", myPiece);
                myBoard.printBoard();
                flipTeamTurn();
            }
        } else {
            throw new InvalidMoveException("Invalid Move!");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for(int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition curPos = new ChessPosition(i,j);
                ChessPiece curPiece = board.getPiece(curPos);
                myBoard.addPiece(curPos, curPiece);
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
    }
}
