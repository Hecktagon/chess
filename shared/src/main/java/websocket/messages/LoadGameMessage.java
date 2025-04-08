package websocket.messages;

import chess.ChessBoard;

public class LoadGameMessage extends ServerMessage{
    ChessBoard chessBoard;
    public LoadGameMessage(ChessBoard board){
        super(ServerMessageType.LOAD_GAME);
        chessBoard = board;
    }
}
