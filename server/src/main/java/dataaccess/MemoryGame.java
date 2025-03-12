package dataaccess;

import chess.ChessGame;
import model.GameData;
import exception.*;

import java.util.*;

public class MemoryGame implements GameDAO{

    private final HashMap<Integer, GameData> games = new HashMap<>();

    public GameData createGame(String gameName) throws ResponseException{
        int gameID = games.size();
        GameData game = new GameData(null, null, gameID, gameName, new ChessGame());
        games.put(gameID, game);
        return game;
    }

    public Collection<GameData> readGames() throws ResponseException{
        Collection<GameData> gameList = games.values();
        return gameList;
    }

    public GameData updateGame(String userName, ChessGame.TeamColor playerColor, Integer gameID) throws ResponseException{
        System.out.print("Player trying to join as " + playerColor + "\n");
        GameData unupdatedGame = games.get(gameID);
        if(unupdatedGame == null){
            throw new ResponseException(400,  "Error: bad request, game was null");
        }
        GameData updatedGame;
        if(Objects.equals(playerColor, ChessGame.TeamColor.BLACK) && unupdatedGame.blackUsername() == null) {
            updatedGame = new GameData(unupdatedGame.whiteUsername(), userName, unupdatedGame.gameID(),
            unupdatedGame.gameName(), unupdatedGame.chessGame());

            games.remove(gameID);
            games.put(updatedGame.gameID(), updatedGame);
            return updatedGame;
        }
        if (Objects.equals(playerColor, ChessGame.TeamColor.WHITE) && unupdatedGame.whiteUsername() == null) {
            updatedGame = new GameData(userName, unupdatedGame.blackUsername(), unupdatedGame.gameID(),
            unupdatedGame.gameName(), unupdatedGame.chessGame());

            games.remove(gameID);
            games.put(updatedGame.gameID(), updatedGame);
            return updatedGame;
        }
        if (!Objects.equals(playerColor, ChessGame.TeamColor.WHITE) && !Objects.equals(playerColor, ChessGame.TeamColor.BLACK)) {

            throw new ResponseException(400, "Error: bad request");
        }
        throw new ResponseException(403, "Error: already taken");
    }

    public void clearGames() throws ResponseException{
        games.clear();
    }
}

