package websocket;

import exception.ResponseException;
import java.io.IOException;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

@WebSocket
public class WebSocketHandler {
        WebSocketSessions sessions;

        @OnWebSocketError
        void onError(Throwable throwable){

        }

        @OnWebSocketMessage
        void onMessage(Session session, String userCommandJson) {
            UserGameCommand command = new Gson().fromJson(userCommandJson, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, session);
                case MAKE_MOVE -> makeMove(command, session);
                case LEAVE -> leaveGame(command, session);
                case RESIGN -> resignGame(command, session);
            }
        }

        //TODO: I am unsure what these functions are supposed to take in/return.
        void connect(UserGameCommand command, Session rootSession){

        }
        void makeMove(UserGameCommand command, Session rootSession){

        }
        void leaveGame(UserGameCommand command, Session rootSession){

        }
        void resignGame(UserGameCommand command, Session rootSession){

        }

        //TODO: are these functions called in the above functions?
        void sendMessage(String message, Session session){

        }
        void broadcastMessage(Integer gameID, String message, Session exceptThisSession){

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
