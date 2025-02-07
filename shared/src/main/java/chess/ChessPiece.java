package chess;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */


public class ChessPiece {
    ChessGame.TeamColor teamColor;
    ChessPiece.PieceType pieceType;
    boolean justMoved;
    int numMoves;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        teamColor = pieceColor;
        pieceType = type;
        numMoves = 0;
        justMoved = false;
    }

    public void setJustMoved(boolean just) {
        this.justMoved = just;
    }

    public void moved(){
        numMoves++;
    }

    public void printMoves(ChessBoard board, ChessPosition myPos, HashSet<ChessMove> moves){
        System.out.print("\n");
        for(int i = 8; i > 0; i--){
            for(int j = 1; j <= 8; j++){
                ChessPosition curPos = new ChessPosition(i, j);
                ChessPiece curPiece = (ChessPiece) board.getPiece(curPos);
                System.out.print("|");
                if (curPiece == null && moves.contains(new ChessMove(myPos, curPos, null))){
                    System.out.print("#");
                } else {
                    System.out.print(board.shortcutMap(curPiece));
                }
            }
            System.out.print("|\n");
        }
        System.out.print("\n");
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Moves typeMove(ChessPiece.PieceType type){
        HashMap<PieceType, Moves> typeMap = new HashMap<>();

        typeMap.put(PieceType.QUEEN, new MovesQueen());
        typeMap.put(PieceType.KING, new MovesKing());
        typeMap.put(PieceType.PAWN, new MovesPawn());
        typeMap.put(PieceType.KNIGHT, new MovesKnight());
        typeMap.put(PieceType.BISHOP, new MovesBishop());
        typeMap.put(PieceType.ROOK, new MovesRook());

        return typeMap.get(type);
    }

    private ChessPiece.PieceType[] promotionsArray(boolean promotes){
        if (promotes){
            return new ChessPiece.PieceType[] {PieceType.QUEEN, PieceType.KNIGHT, PieceType.ROOK, PieceType.BISHOP};
        } else {
            return new ChessPiece.PieceType[] {null};
        }
    }

    public Collection<ChessMove> castle(ChessBoard board, ChessPosition pos){
        HashSet<ChessMove> castles = new HashSet<>();
        ChessPiece king = board.getPiece(pos);
        ChessPiece negRook = board.getPiece(new ChessPosition(pos.getRow(),1));
        ChessPiece posRook = board.getPiece(new ChessPosition(pos.getRow(),8));
        ChessPiece blocking;
        boolean blockedNeg = false;
        boolean blockedPos = false;

        // Check if neg path from king to rook is clear
        for(int i = 2; i < 5; i++){
            blocking = board.getPiece(new ChessPosition(pos.getRow(), i));
            if (blocking != null){
                blockedNeg = true;
            }
        }

        // Check if pos path from king to rook is clear
        for(int i = 6; i < 8; i++){
            blocking = board.getPiece(new ChessPosition(pos.getRow(), i));
            if (blocking != null){
                blockedPos = true;
            }
        }

        // Check if king & rook(s) are in position and haven't moved
        if (king.numMoves == 0) {
            if (king.teamColor == ChessGame.TeamColor.WHITE && pos.getRow() == 1 && pos.getColumn() == 5 ||
                king.teamColor == ChessGame.TeamColor.BLACK && pos.getRow() == 8 && pos.getColumn() == 5) {
                if (!blockedPos) {
                    if (posRook != null && posRook.teamColor == king.teamColor && posRook.numMoves == 0) {
                        ChessMove posCastle = new ChessMove(pos, new ChessPosition(pos.getRow(), pos.getColumn() + 2), null);
                        posCastle.setCastle(true);
                        castles.add(posCastle);
                    }
                }
                if (!blockedNeg) {
                    if (negRook != null && negRook.teamColor == king.teamColor && negRook.numMoves == 0) {
                        ChessMove negCastle = new ChessMove(pos, new ChessPosition(pos.getRow(), pos.getColumn() - 2), null);
                        negCastle.setCastle(true);
                        castles.add(negCastle);
                    }
                }
            }
        }
        return castles;
    }

    public ChessMove enPassant(ChessBoard board, ChessPosition pos){
        ChessMove enPass;
        ChessPosition posPosition = new ChessPosition(pos.getRow(), pos.getColumn() + 1);
        ChessPosition negPosition = new ChessPosition(pos.getRow(), pos.getColumn() - 1);;
        ChessPiece pawn = board.getPiece(pos);
        int direction = pawn.teamColor == ChessGame.TeamColor.WHITE ? 1 : -1;

        if(pawn.teamColor == ChessGame.TeamColor.WHITE && pos.getRow() == 5 ||
            pawn.teamColor == ChessGame.TeamColor.BLACK && pos.getRow() == 4){
            // pawn positive
            if (posPosition.posInBounds()) {
                ChessPiece posPiece = board.getPiece(posPosition);
                if (posPiece != null && posPiece.pieceType == PieceType.PAWN
                    && posPiece.teamColor != pawn.teamColor && posPiece.justMoved){
                    enPass = new ChessMove(pos, new ChessPosition(pos.getRow() + direction, pos.getColumn() + 1), null);
                    enPass.setEnPassant(true);
                    return enPass;
                }
            }
            // pawn negative
            if (negPosition.posInBounds()) {
                ChessPiece negPiece = board.getPiece(negPosition);
                if (negPiece != null && negPiece.pieceType == PieceType.PAWN
                        && negPiece.teamColor != pawn.teamColor && negPiece.justMoved){
                    enPass = new ChessMove(pos, new ChessPosition(pos.getRow() + direction, pos.getColumn() - 1), null);
                    enPass.setEnPassant(true);
                    return enPass;
                }
            }
        }
        return null;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        /* STRUCTURE:
        an array of arrays of directions to move, as well as a range of how many spaces they can move.
        [
        [to_row, to_col, range (if 0, unlimited)], promotes (0 or 1),
        [to_row, to_col, range, promotes],
        [to_row, to_col, range, promotes]
        ]
        */

        HashSet<ChessMove> moves = new HashSet<>();

        ChessPiece myPiece = board.getPiece(myPosition);
        ChessPiece.PieceType myType = myPiece.getPieceType();
        Moves myMoves = typeMove(myType);
        int[][] directions = myMoves.pieceMoves(board, myPosition);
        int myY = myPosition.getRow();
        int myX = myPosition.getColumn();

        // loop through each direction the piece can move, i.e. each inner array
        eachDirection:
        for(int[]direction : directions){
            int yDir = direction[0];
            int xDir = direction[1];
            int range = direction[2] == 0 ? 10 : direction[2];
            boolean promotes = direction[3] == 1;
            ChessPiece.PieceType[] promos = promotionsArray(promotes);

            // the position of our target space, will be incremented in our current direction in the next loop.
            int targetY = myY + yDir;
            int targetX = myX + xDir;
            ChessPosition targetPos;
            ChessPiece targetPiece;
            ChessMove curMove;

            // check spaces in that direction until we hit the piece's range
            for(int i = 0; i < range; i++){
                targetPos = new ChessPosition(targetY, targetX);
                targetY += yDir;
                targetX += xDir;

                if (targetPos.posInBounds()){
                    int promoCount = 1;
                    // make a new move and decide if valid for each promotion (either null or Q, N, B, R)
                    for(ChessPiece.PieceType promo : promos) {
                        curMove = new ChessMove(myPosition, targetPos, promo);
                        promoCount++;

                        boolean[] isValid = board.isValidMove(curMove);
                        boolean valid = isValid[0];
                        boolean captured = isValid[1];

                        if (valid){
                            moves.add(curMove);
                            if (captured){
                                // this is statement and the promoCount counter prevent the loop from breaking on a capture until all promotions have been handled
                                if (myPiece.getPieceType() == PieceType.PAWN && promoCount <= 4){
                                    continue;
                                }
                                continue eachDirection;
                            }
                        } else {
                            continue eachDirection;
                        }
                    }
                }
            }
        }
        if (pieceType == PieceType.KING){
            moves.addAll(castle(board, myPosition));
        }
        if (pieceType == PieceType.PAWN){
            ChessMove enPass = enPassant(board, myPosition);
            if (enPass != null) {
                moves.add(enPass);
            }
        }
//        printMoves(board, myPosition, moves);
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", pieceType=" + pieceType +
                ", justMoved=" + justMoved +
                ", numMoves=" + numMoves +
                '}';
    }
}
