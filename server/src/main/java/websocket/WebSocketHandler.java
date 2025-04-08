package websocket;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

@WebSocket
public class WebSocketHandler {
        WebSocketSessions sessions;
        AuthDAO authDAO;
        GameDAO gameDAO;
        UserDAO userDAO;

        public WebSocketHandler(){
            sessions = new WebSocketSessions();
        }

        @OnWebSocketConnect
        void onConnect(Session rootSession) throws ResponseException{
            System.out.println(rootSession + " Successfully connected to websocket!");
            authDAO = new SqlAuth();
            gameDAO = new SqlGame();
            userDAO = new SqlUser();
        }

        @OnWebSocketError
        void onError(Session rootSession, Throwable throwable) throws ResponseException{
            ErrorMessage errMessage = new ErrorMessage("Connection Error: " + throwable.getMessage());

            try{
                rootSession.getRemote().sendString(errMessage.getErrMessage());
            } catch (IOException e) {
                throw new ResponseException(500, "Failed to send error to client: " + e.getMessage());
            }
        }

        @OnWebSocketMessage
        void onMessage(Session session, String userCommandJson) throws ResponseException{
            UserGameCommand command = new Gson().fromJson(userCommandJson, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, session);
                case MAKE_MOVE -> makeMove(new Gson().fromJson(userCommandJson, MakeMoveCommand.class), session);
                case LEAVE -> leaveGame(command, session);
                case RESIGN -> resignGame(command, session);
            }
        }

        void connect(UserGameCommand command, Session rootSession) throws ResponseException{
            // Creating the LoadGameMessage for the user, then sending it
            GameData gameData = gameDAO.getGame(command.getGameID());
            LoadGameMessage gameMessage = new LoadGameMessage(gameData.chessGame());
            sendMessage(gameMessage, rootSession);

            // Creating the Notification for all other in-game users (including player color if user joins as player), and broadcasting it
            UserData userData = userDAO.getUser(command.getAuthToken());
            NotificationMessage notification;
            String userColor = (Objects.equals(userData.username(), gameData.whiteUsername())) ? "white":
                    (Objects.equals(userData.username(), gameData.blackUsername())) ? "black" : null;

            if (userColor != null) {
                notification = new NotificationMessage(userData.username() + " joined the game as " + userColor + ".");
            } else{
                notification = new NotificationMessage(userData.username() + " is observing the game.");
            }
            broadcastMessage(gameData.gameID(), notification, rootSession);
        }

        void makeMove(MakeMoveCommand moveCommand, Session rootSession) throws ResponseException{
            GameData gameData = gameDAO.getGame(moveCommand.getGameID());
            ChessGame game = gameData.chessGame();

        }

        void leaveGame(UserGameCommand command, Session rootSession){

        }

        void resignGame(UserGameCommand command, Session rootSession){

        }


        void sendMessage(ServerMessage message, Session session) throws ResponseException{
            String messageJSON = new Gson().toJson(message);
            try {
                session.getRemote().sendString(messageJSON);
            } catch (IOException e) {
                throw new ResponseException(500, "Failed to send LoadGameMessage to client: " + e.getMessage());
            }
        }
        void broadcastMessage(Integer gameID, ServerMessage message, Session exceptThisSession) throws ResponseException{
            HashSet<Session> gameSessions = sessions.getSessionsForGame(gameID);
            for (Session session : gameSessions){
                if (session != exceptThisSession) {
                    sendMessage(message, session);
                }
            }
        }
}



// petshop's WebSocketHandler:

//package server.websocket;
//
//import com.google.gson.Gson;
//import dataaccess.DataAccess;
//import exception.ResponseException;
//import org.eclipse.jetty.websocket.api.Session;
//import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
//import org.eclipse.jetty.websocket.api.annotations.WebSocket;
//import webSocketMessages.Action;
//import webSocketMessages.Notification;
//

//import java.util.Timer;
//
//
//@WebSocket
//public class WebSocketHandler {
//
//    private final ConnectionManager connections = new ConnectionManager();
//
//    @OnWebSocketMessage
//    public void onMessage(Session session, String message) throws IOException {
//        Action action = new Gson().fromJson(message, Action.class);
//        switch (action.type()) {
//            case ENTER -> enter(action.visitorName(), session);
//            case EXIT -> exit(action.visitorName());
//        }
//    }
//
//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(visitorName, session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//}
