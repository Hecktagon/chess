package chess;

public class MovesKnight implements Moves{
    public int[][] pieceMoves(ChessBoard board, ChessPosition myPos){
        int[][] directions = {
                {1, 2, 1, 0},
                {1, -2, 1, 0},
                {-1, 2, 1, 0},
                {-1, -2, 1, 0},
                {2, 1, 1, 0},
                {2, -1, 1, 0},
                {-2, 1, 1, 0},
                {-2, -1, 1, 0}
        };

        return directions;
    }

}
