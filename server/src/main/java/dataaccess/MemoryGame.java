package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

import java.util.*;

public class MemoryGame implements GameDAO{

    private final HashMap<Integer, GameData> games = new HashMap<>();

    public GameData createGame(String gameName) throws ResponseException {
        int gameID = games.size();
        GameData game = new GameData(null, null, gameID, gameName, new ChessGame());
        games.put(gameID, game);
        return game;
    }

    public Collection<GameData> readGames() throws ResponseException{
        return games.values();
    }

    public GameData getGame(Integer gameID){
        if (games.containsKey(gameID)){
            return games.get(gameID);
        }
        return null;
    }

    public GameData updateGame(GameData gameData){
        if (games.containsKey(gameData.gameID())){
            games.remove(gameData.gameID());
            games.put(gameData.gameID(), gameData);
            return gameData;
        }
        return null;
    }

    public void clearGames() throws ResponseException{
        games.clear();
    }
}

