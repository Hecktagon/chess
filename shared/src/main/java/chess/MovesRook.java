package chess;

public class MovesRook implements Moves{
    public int[][] pieceMoves(ChessBoard board, ChessPosition myPos){
        int[][] directions = {
                {1, 0, 0, 0},
                {-1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, -1, 0, 0},
        };

        return directions;
    }
}
