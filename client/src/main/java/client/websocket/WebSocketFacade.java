package client.websocket;

import chess.ChessMove;
import client.Client;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class WebSocketFacade extends Endpoint implements MessageHandler.Whole<String> {
        public Session session;
        GameHandler gameHandler;

        public WebSocketFacade() throws ResponseException{
            try {
                URI uri = new URI("ws://localhost:8080/ws");
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                this.session = container.connectToServer(this, uri);
            }catch (Exception e){
                throw new ResponseException(500, "Error: Websocket connection failed: \n" + e);
            }
        }

        @Override //overridden from endpoint
        public void onOpen(Session session, EndpointConfig endpointConfig){}

    // OUTGOING MESSAGES TO SERVER
        public void connect(String authToken, Integer gameID) throws ResponseException{
            System.out.println(SET_TEXT_COLOR_GREEN + "CONNECTING TO GAME" + RESET_TEXT_COLOR);
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            sendMessage(new Gson().toJson(command));
        }
        public void makeMove(String authToken, Integer gameID, ChessMove chessMove) throws ResponseException{
            MakeMoveCommand moveCommand = new MakeMoveCommand(chessMove, authToken, gameID);
            sendMessage(new Gson().toJson(moveCommand));
        }
        public void leaveGame(String authToken, Integer gameID) throws ResponseException{
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            sendMessage(new Gson().toJson(command));
        }
        public void resignGame(String authToken, Integer gameID) throws ResponseException{
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            sendMessage(new Gson().toJson(command));
        }

        public void sendMessage(String serverCommandJSON) throws ResponseException{
            try {

                this.session.getBasicRemote().sendText(serverCommandJSON);
            } catch (IOException e) {
                throw new ResponseException(500, "Error: failed to send websocket command to server: \n" + e);
            }
        }

    // PROCESS INCOMING MESSAGES FROM SERVER:
        @Override
        public void onMessage(String message){
            System.out.println(SET_TEXT_COLOR_GREEN + "RECEIVED MESSAGE FROM SERVER" + RESET_TEXT_COLOR);
            ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
            if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){
                LoadGameMessage gameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                gameHandler.updateGame(gameMessage.getChessGame(), null);
            } else {
                String clientMessage;
                ServerMessage.ServerMessageType type;
                if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
                    ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                    clientMessage = errorMessage.getErrMessage();
                    type = ServerMessage.ServerMessageType.ERROR;
                } else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
                    NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                    clientMessage = notification.getMessage();
                    type = ServerMessage.ServerMessageType.NOTIFICATION;
                } else {
                    System.out.println("\n\n\n Somehow we ended up with an invalid ServerMessageType \n\n\n");
                    return;
                }
                gameHandler.printMessage(clientMessage, type);
            }
        }

}