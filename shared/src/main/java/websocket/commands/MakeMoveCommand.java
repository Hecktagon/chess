package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    ChessMove move;
    public MakeMoveCommand(ChessMove chessMove, String authtoken, Integer gameID){
        super(CommandType.MAKE_MOVE, authtoken, gameID);
        move = chessMove;
    }

    public ChessMove getMove() {
        return move;
    }
}
