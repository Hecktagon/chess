package websocket;

import org.eclipse.jetty.websocket.api.Session;
import java.util.HashMap;
import java.util.HashSet;

public class WebSocketSessions {
        private final HashMap<Integer, HashSet<Session>> sessionMap = new HashMap<>();
        private final HashMap<Session, Integer> sessionToGame = new HashMap<>();

        void addSessionToGame(Integer gameID, Session session){
            if (!sessionMap.containsKey(gameID)) {
                sessionMap.put(gameID, new HashSet<>());
            }
            try {
                HashSet<Session> gameSessions = sessionMap.get(gameID);
                gameSessions.add(session);
                sessionToGame.put(session, gameID);
            } catch (Exception e) {
                System.out.println("Failed to add to game: no such game.");
            }
        }
        void removeSessionFromGame(Integer gameID, Session session){
            try {
                HashSet<Session> gameSessions = sessionMap.get(gameID);
                gameSessions.remove(session);
                sessionToGame.remove(session);
            } catch (Exception e) {
                System.out.println("Failed to leave game: no such game.");
            }
        }

        HashSet<Session> getSessionsForGame(Integer gameID){
            if (!sessionMap.containsKey(gameID)) {
                sessionMap.put(gameID, new HashSet<>());
            }
            return sessionMap.get(gameID);
        }

        void removeSessionForDisconnect(Session session){
            if (sessionToGame.containsKey(session)){
                Integer gameID = sessionToGame.get(session);
                removeSessionFromGame(gameID, session);
            }
        }
}



// petshop's ConnectionManager:

//package server.websocket;
//
//import org.eclipse.jetty.websocket.api.Session;
//import webSocketMessages.Notification;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class ConnectionManager {
//    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
//
//    public void add(String visitorName, Session session) {
//        var connection = new Connection(visitorName, session);
//        connections.put(visitorName, connection);
//    }
//
//    public void remove(String visitorName) {
//        connections.remove(visitorName);
//    }
//
//    public void broadcast(String excludeVisitorName, Notification notification) throws IOException {
//        var removeList = new ArrayList<Connection>();
//        for (var c : connections.values()) {
//            if (c.session.isOpen()) {
//                if (!c.visitorName.equals(excludeVisitorName)) {
//                    c.send(notification.toString());
//                }
//            } else {
//                removeList.add(c);
//            }
//        }
//
//        // Clean up any connections that were left open.
//        for (var c : removeList) {
//            connections.remove(c.visitorName);
//        }
//    }
//}