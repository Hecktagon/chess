package chess;

import java.util.HashSet;

public interface Moves {
    public HashSet<ChessMove> pieceMoves(ChessBoard board,ChessPosition position);
}
