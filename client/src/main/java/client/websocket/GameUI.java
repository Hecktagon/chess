package client.websocket;

import chess.ChessGame;
import chess.ChessPosition;
import ui.DisplayChessBoard;
import websocket.messages.ServerMessage;

public class GameUI implements GameHandler{
    WebSocketFacade webSocketFacade;

    @Override
    public String[][] updateGame(ChessGame game, ChessPosition posValids){
        if (posValids == null){
            DisplayChessBoard.printGame();
        }
    }

    @Override
    public void printMessage(String message, ServerMessage.ServerMessageType type){

    }
}
