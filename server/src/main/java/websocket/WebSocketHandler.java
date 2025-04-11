package websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import dataaccess.*;
import exception.ResponseException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import spark.Response;
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

        @OnWebSocketClose
        public void onClose(Session rootSession, int integer, String someJSON) {
            sessions.removeSessionForDisconnect(rootSession);
        }

        @OnWebSocketConnect
        public void onConnect(Session rootSession) throws ResponseException{
            System.out.println(rootSession.getLocalAddress() + " Successfully connected to websocket!");
            authDAO = new SqlAuth();
            gameDAO = new SqlGame();
            userDAO = new SqlUser();
        }

        @OnWebSocketError
        public void onError(Session rootSession, Throwable throwable) throws ResponseException{
            ErrorMessage errMessage = new ErrorMessage("Connection Error: " + throwable.toString());

            try{
                rootSession.getRemote().sendString(new Gson().toJson(errMessage));
            } catch (IOException e) {
                throw new ResponseException(500, "Failed to send error to client: " + e.getMessage());
            }
            throwable.printStackTrace();
//            throw new ResponseException(400, "Websocket onError Error: " + throwable.toString());
        }

        @OnWebSocketMessage
        public void onMessage(Session session, String userCommandJson) throws ResponseException{
            System.out.println("MADE IT TO WEBSOCKET ONMESSAGE");
            try {
                UserGameCommand command = new Gson().fromJson(userCommandJson, UserGameCommand.class);
                AuthData auth = authDAO.getAuth(command.getAuthToken());
                if (auth == null) {
                    throw new ResponseException(400, "Unauthorized.");
                }
                GameData gameData = gameDAO.getGame(command.getGameID());
                if (gameData == null){
                    throw new ResponseException(400, "No such game.");
                }
                switch (command.getCommandType()) {
                    case CONNECT -> connect(command, session);
                    case MAKE_MOVE -> makeMove(new Gson().fromJson(userCommandJson, MakeMoveCommand.class), session);
                    case LEAVE -> leaveGame(command, session);
                    case RESIGN -> resignGame(command, session);
                }
            } catch (Exception ex){
                try{
                    ErrorMessage errMessage = new ErrorMessage("Error: " + ex.getMessage());
                    session.getRemote().sendString(new Gson().toJson(errMessage));
                } catch (IOException e) {
                    throw new ResponseException(500, "Failed to send error to client: " + e.getMessage());
                }
            }
        }

        void connect(UserGameCommand command, Session rootSession) throws ResponseException{
//            System.out.println("MADE IT TO WEBSOCKET CONNECT");
            // Creating the LoadGameMessage for the user, then sending it
            GameData gameData = gameDAO.getGame(command.getGameID());
            sessions.addSessionToGame(gameData.gameID(), rootSession);

            LoadGameMessage gameMessage = new LoadGameMessage(gameData.chessGame());
            sendMessage(gameMessage, rootSession);

            // Creating the Notification for all other in-game users (including player color if user joins as player), and broadcasting it
            
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            NotificationMessage notification;
            String userColor = findUserColor(authData, gameData);

            if (userColor != null) {
                notification = new NotificationMessage(authData.username() + " joined the game as " + userColor + ".");
            } else{
                notification = new NotificationMessage(authData.username() + " is observing the game.");
            }
            broadcastMessage(gameData.gameID(), notification, rootSession, true);
        }

        void makeMove(MakeMoveCommand moveCommand, Session rootSession) throws ResponseException{
            GameData gameData = gameDAO.getGame(moveCommand.getGameID());
            AuthData authData = authDAO.getAuth(moveCommand.getAuthToken());
            ChessGame game = gameData.chessGame();

            // if move is valid, make the move.
            String userColor = findUserColor(authData, gameData);
            boolean correctTurn = (Objects.equals(userColor, "white") && game.getTeamTurn() == ChessGame.TeamColor.WHITE) ||
                    (Objects.equals(userColor, "black") && game.getTeamTurn() == ChessGame.TeamColor.BLACK);
            boolean isPlayer = userColor != null;
            boolean isGameOver = game.isOver();
            if(game.isValidMove(moveCommand.getMove()) && !isGameOver && isPlayer && correctTurn){
                try {
                    game.makeMove(moveCommand.getMove());
                } catch (InvalidMoveException e){
                    throw new ResponseException(401, "Invalid move");
                }

                // if in check, checkmate, or stalemate, make notification. Mark game as over if game ended.
                NotificationMessage checkNotification = null;
                if (game.isInCheckmate(ChessGame.TeamColor.WHITE)){
                    checkNotification = new NotificationMessage(gameData.blackUsername() + " put " +
                            gameData.whiteUsername() + " in Checkmate!" +  "\nBlack Wins!");
                    game.gameOver();
                } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)){
                    checkNotification = new NotificationMessage(gameData.whiteUsername() + " put " +
                            gameData.blackUsername() + " in Checkmate!" +  "\nWhite Wins!");
                    game.gameOver();
                } else if ((game.isInStalemate(ChessGame.TeamColor.WHITE) && game.getTeamTurn() == ChessGame.TeamColor.WHITE) ||
                        (game.isInStalemate(ChessGame.TeamColor.BLACK) && game.getTeamTurn() == ChessGame.TeamColor.BLACK)){
                    checkNotification = new NotificationMessage("Stalemate!");
                    game.gameOver();
                } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                    checkNotification = new NotificationMessage(gameData.whiteUsername() + "is in Check");
                } else if (game.isInCheck(ChessGame.TeamColor.BLACK)){
                    checkNotification = new NotificationMessage(gameData.blackUsername() + "is in Check");
                }

                // make the move in the database
                GameData updatedGame = new GameData(gameData.whiteUsername(), gameData.blackUsername(),
                        gameData.gameID(), gameData.gameName(), game);
                GameData gameAfterUpdate = gameDAO.updateGame(updatedGame);

                // send the updated chessboard to everyone in the game
                LoadGameMessage gameMessage = new LoadGameMessage(gameAfterUpdate.chessGame());
                String turnUser = (game.getTeamTurn() == ChessGame.TeamColor.WHITE) ? gameData.blackUsername() : gameData.whiteUsername();
                broadcastMessage(gameData.gameID(), gameMessage, rootSession, false);

                // make and send move notification to everyone else
                String moveString = game.toChessNotation(moveCommand.getMove());

                broadcastMessage(gameData.gameID(), new NotificationMessage(turnUser + " made move : " + moveString), rootSession, true);

                // send notification for check, checkmate, or stalemate
                if (checkNotification != null){
                    broadcastMessage(gameData.gameID(), checkNotification, rootSession, false);
                }
            } else {
                throw new ResponseException(401, "Invalid Move");
            }
        }

        void leaveGame(UserGameCommand command, Session rootSession) throws ResponseException {
            GameData gameData = gameDAO.getGame(command.getGameID());
            AuthData authData = authDAO.getAuth(command.getAuthToken());

            // update database to remove user from game if they are a player
            String userColor = findUserColor(authData, gameData);
            if (Objects.equals(userColor, "white")){
                GameData updatedGameData = new GameData(null, gameData.blackUsername(),
                        gameData.gameID(), gameData.gameName(), gameData.chessGame());
                gameDAO.updateGame(updatedGameData);
            } else if (Objects.equals(userColor, "black")){
                GameData updatedGameData = new GameData(gameData.whiteUsername(), null,
                        gameData.gameID(), gameData.gameName(), gameData.chessGame());
                gameDAO.updateGame(updatedGameData);
            }

            // remove session from Sessions:
            sessions.removeSessionFromGame(gameData.gameID(), rootSession);

            // if a player or observer left, notify everyone else in the game
            NotificationMessage leaveNotification = new NotificationMessage(authData.username() + " left the game.");
            broadcastMessage(gameData.gameID(), leaveNotification, rootSession, true);

        }

        void resignGame(UserGameCommand command, Session rootSession) throws ResponseException{
            GameData gameData = gameDAO.getGame(command.getGameID());
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            ChessGame game = gameData.chessGame();

            String playerColor = findUserColor(authData, gameData);
            if (playerColor != null && !game.isOver()) {

                // mark the game as over
                game.gameOver();

                //update gameover in the database
                GameData resignedGame = new GameData(gameData.whiteUsername(), gameData.blackUsername(),
                        gameData.gameID(), gameData.gameName(), game);
                gameDAO.updateGame(resignedGame);

                // notify everyone of resignation
                NotificationMessage resignMessage = new NotificationMessage(authData.username() + " resigned!");
                broadcastMessage(gameData.gameID(), resignMessage, rootSession, false);
            }else {
                ErrorMessage resignFailed = new ErrorMessage("Error: you can't resign now");
                sendMessage(resignFailed, rootSession);
            }
        }

        String findUserColor(AuthData authData, GameData gameData){
            return (Objects.equals(authData.username(), gameData.whiteUsername())) ? "white":
                    (Objects.equals(authData.username(), gameData.blackUsername())) ? "black" : null;
        }

        void sendMessage(ServerMessage message, Session session) throws ResponseException{
            String messageJSON = new Gson().toJson(message);
            try {
                session.getRemote().sendString(messageJSON);
            } catch (IOException e) {
                throw new ResponseException(500, "Failed to send LoadGameMessage to client: " + e.getMessage());
            }
        }
        void broadcastMessage(Integer gameID, ServerMessage message, Session rootSession, boolean exclude) throws ResponseException {
            try {
                HashSet<Session> gameSessions = sessions.getSessionsForGame(gameID);
                for (Session session : gameSessions) {
                    // if exclude is false, the if condition always evaluates to true, meaning everyone gets the message
                    if (!exclude || session != rootSession) {
                        sendMessage(message, session);
                    }
                }
            } catch (Exception e) {
                throw new ResponseException(501, "Broadcast failed with error: " + e);
            }
        }
}