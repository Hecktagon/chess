package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    ChessGame game;
    public LoadGameMessage(ChessGame chessGame){
        super(ServerMessageType.LOAD_GAME);
        game = chessGame;
    }

    public ChessGame getChessGame() {
        return game;
    }
}
