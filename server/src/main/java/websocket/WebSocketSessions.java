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