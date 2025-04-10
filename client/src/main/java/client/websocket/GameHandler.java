package client.websocket;

import chess.ChessGame;
import chess.ChessPosition;
import websocket.messages.ServerMessage;

public interface GameHandler {
        void updateGame(ChessGame game, ChessPosition posValids, ChessGame.TeamColor team);
        void printMessage(String message, ServerMessage.ServerMessageType type);
}
