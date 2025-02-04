package chess;

public class MovesBishop implements Moves{
    public int[][] pieceMoves(ChessBoard board, ChessPosition myPos){
        int[][] directions = {
                {1, 1, 0, 0},
                {1, -1, 0, 0},
                {-1, 1, 0, 0},
                {-1, -1, 0, 0},
        };

        return directions;
    }
}
