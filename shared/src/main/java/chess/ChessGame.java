package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor turn;
    ChessBoard myBoard;
    int gameMoves;
    ChessPiece mostRecent;

    public ChessGame() {
        turn = TeamColor.WHITE;
        myBoard = new ChessBoard();
        myBoard.resetBoard();
        setTeamTurn(TeamColor.WHITE);
        gameMoves = 0;
        mostRecent = null;
    }

    public int getGameMoves() {
        return gameMoves;
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

    public boolean isValidMove(ChessMove move){
        Collection<ChessMove> valids = validMoves(move.getStartPosition());
        return valids.contains(move);
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

        for(ChessMove move : allMoves){
            if(move.isCastle()){


                if (endangersKing(move)){
                    allMoves.remove(move);
                } else {
                    System.out.printf("\n added Castle:\n%s\n", move);
                }

            } else if (move.isEnPassant()) {
               ChessPosition pawnStart = move.getStartPosition();
               ChessPiece pawn = myBoard.getPiece(pawnStart);
               ChessPosition pawnEnd = move.getEndPosition();
               int direction = pawn.teamColor == ChessGame.TeamColor.WHITE ? 1 : -1;

               ChessPosition enemyPos = new ChessPosition(pawnEnd.getRow() - direction, pawnEnd.getColumn());
               ChessPiece enemyPawn = myBoard.getPiece(enemyPos);

               myBoard.addPiece(enemyPos, null);
               if(endangersKing(move)){
                   allMoves.remove(move);
               } else {
                   System.out.printf("\n added En Passant:\n%s\n", move);
               }
                myBoard.addPiece(enemyPos, enemyPawn);

            }
        }

        return allMoves;
    }

    public boolean endangersKing(ChessMove move){
        ChessPosition start = move.getStartPosition();
        ChessPiece myPiece = myBoard.getPiece(start);
        ChessPosition end = move.getEndPosition();
        ChessPiece endPiece = myBoard.getPiece(end);
        boolean causesCheck;

        myBoard.addPiece(start, null);
        myBoard.addPiece(end, myPiece);

        if(move.isCastle()) {
            ChessPosition kingStart = move.getStartPosition();
            ChessPosition kingEnd = move.getEndPosition();

            // test move rook
            ChessPosition posRookStart = kingEnd.getColumn() > kingStart.getColumn() ?
                    new ChessPosition(kingStart.getRow(), 8) : new ChessPosition(kingStart.getRow(), 1);
            ChessPosition posRookEnd = kingEnd.getColumn() > kingStart.getColumn() ?
                    new ChessPosition(kingStart.getRow(), 6) : new ChessPosition(kingStart.getRow(), 4);

            ChessPiece posRook = myBoard.getPiece(posRookStart);
            myBoard.addPiece(posRookStart, null);
            myBoard.addPiece(posRookEnd, posRook);

            System.out.print("\ntesting Castle:\n");
            myBoard.printBoard();

            causesCheck = castleBlocked(move);
            System.out.printf("\nARE WE IN CHECK? %s\n", causesCheck);

            myBoard.addPiece(posRookStart, posRook);
            myBoard.addPiece(posRookEnd, null);
        } else {
            causesCheck = isInCheck(myPiece.getTeamColor());
        }

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
        if(valids != null) {
            for (ChessMove valid : valids) {
                if (valid.equals(move)) {
                    move = valid;
                }
            }
        }
//        System.out.print("\nDOING MOVE:\n");
//        System.out.print(move + "\n");

        if (valids != null && valids.contains(move) && start.posInBounds() && end.posInBounds()){
            ChessPiece myPiece = myBoard.getPiece(start);
            if(myPiece != null && myPiece.getTeamColor() == turn) {
                // Castle
                if(move.isCastle()){
                    System.out.print("\nDOING CASTLE\n");
                    ChessPosition kingStart = move.getStartPosition();
                    ChessPosition kingEnd = move.getEndPosition();

                    // test move rook
                    ChessPosition posRookStart = kingEnd.getColumn() > kingStart.getColumn() ?
                            new ChessPosition(kingStart.getRow(), 8) : new ChessPosition(kingStart.getRow(), 1);
                    ChessPosition posRookEnd = kingEnd.getColumn() > kingStart.getColumn() ?
                            new ChessPosition(kingStart.getRow(), 6) : new ChessPosition(kingStart.getRow(), 4);

                    ChessPiece posRook = myBoard.getPiece(posRookStart);
                    myBoard.addPiece(posRookStart, null);
                    myBoard.addPiece(posRookEnd, posRook);
                }

                // En Passant
                if(move.isEnPassant()){
                    System.out.print("\nDOING EN PASSANT\n");
                    ChessPosition pawnStart = move.getStartPosition();
                    ChessPiece pawn = myBoard.getPiece(pawnStart);
                    ChessPosition pawnEnd = move.getEndPosition();
                    int direction = pawn.teamColor == ChessGame.TeamColor.WHITE ? 1 : -1;

                    ChessPosition enemyPos = new ChessPosition(pawnEnd.getRow() - direction, pawnEnd.getColumn());

                    myBoard.addPiece(enemyPos, null);
                }

                myBoard.addPiece(start, null);
                if (move.getPromotionPiece() == null){
                    myBoard.addPiece(end, myPiece);
                } else {
                    myBoard.addPiece(end, new ChessPiece(myPiece.getTeamColor(), move.getPromotionPiece()));
                }
                System.out.printf("\n%s Moved:\n", myPiece);
//                myBoard.printBoard();
                myPiece.moved();
                if (mostRecent != null) {
                    mostRecent.setJustMoved(false);
                }
                myPiece.setJustMoved(true);
                mostRecent = myPiece;
                gameMoves++;
                flipTeamTurn();

            } else {
                throw new InvalidMoveException("Invalid Move!");
            }

        } else {
            throw new InvalidMoveException("Invalid Move!");
        }
    }


    HashSet<ChessPosition> findTeamAttacks(TeamColor team) {
        HashSet<ChessPosition> attacks = new HashSet<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition curPos = new ChessPosition(i, j);
                ChessPiece curPiece = myBoard.getPiece(curPos);
                if (curPiece != null && curPiece.getTeamColor() == team) {
                    Collection<ChessMove> curMoves = curPiece.pieceMoves(myBoard, curPos);
                    for (ChessMove move : curMoves){
                        attacks.add(move.getEndPosition());
                    }
                }
            }
        }
        return attacks;
    }

    boolean teamCanMove(TeamColor team) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition curPos = new ChessPosition(i, j);
                ChessPiece curPiece = myBoard.getPiece(curPos);
                if (curPiece != null && curPiece.getTeamColor() == team && !validMoves(curPos).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    ChessPosition findKing(TeamColor team){
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition curPos = new ChessPosition(i, j);
                ChessPiece curPiece = myBoard.getPiece(curPos);
                if (curPiece != null && curPiece.getTeamColor() == team && curPiece.getPieceType() == ChessPiece.PieceType.KING){
                    return curPos;
                }
            }
        }
        throw new NoSuchElementException("King does not exist!");
    }

    public boolean castleBlocked(ChessMove move){
        ChessPosition kingStart = move.getStartPosition();
        ChessPosition kingEnd = move.getEndPosition();
        boolean castleDirectionPos = kingEnd.getColumn() > kingStart.getColumn();

        ChessPiece king = myBoard.getPiece(kingEnd);
        TeamColor team = king.getTeamColor();
        TeamColor enemyColor = team == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        HashSet<ChessPosition> enemyAttacks = findTeamAttacks(enemyColor);
        if (castleDirectionPos){
            return enemyAttacks.contains(kingStart)
                    || enemyAttacks.contains(new ChessPosition(kingStart.getRow(), kingStart.getColumn() + 1))
                    || enemyAttacks.contains(kingEnd);
        } else {
            return enemyAttacks.contains(kingStart)
                    || enemyAttacks.contains(new ChessPosition(kingStart.getRow(), kingStart.getColumn() - 1))
                    || enemyAttacks.contains(new ChessPosition(kingStart.getRow(), kingStart.getColumn() - 2))
                    || enemyAttacks.contains(kingEnd);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor enemyColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        HashSet<ChessPosition> enemyAttacks = findTeamAttacks(enemyColor);
        ChessPosition kingPos = findKing(teamColor);
        return enemyAttacks.contains(kingPos);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        TeamColor enemyColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        HashSet<ChessPosition> enemyAttacks = findTeamAttacks(enemyColor);
        ChessPosition kingPos = findKing(teamColor);
        boolean canMove = teamCanMove(teamColor);

        return enemyAttacks.contains(kingPos) && !canMove;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !teamCanMove(teamColor) && !isInCheck(teamColor);
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
//        System.out.print("\nBoard Set\n");
        board.printBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
    }

    @Override
    public String toString() {
        return "ChessGame{}";
    }
}

