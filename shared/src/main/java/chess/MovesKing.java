package chess;

public class MovesKing implements Moves{
    public int[][] pieceMoves(ChessBoard board, ChessPosition myPos){
        int[][] directions = {
                {1, 1, 1, 0},
                {1, -1, 1, 0},
                {-1, 1, 1, 0},
                {-1, -1, 1, 0},
                {0, 1, 1, 0},
                {0, -1, 1, 0},
                {1, 0, 1, 0},
                {-1, 0, 1, 0},
        };

        return directions;
    }
}
