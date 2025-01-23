package chess;

import java.util.HashSet;

public interface Moves {
    HashSet<ChessMove> pieceMoves(ChessBoard board,ChessPosition position);
}
