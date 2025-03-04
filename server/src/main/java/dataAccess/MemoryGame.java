package dataAccess;

import chess.ChessGame;
import model.GameData;
import exception.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class MemoryGame implements GameDAO{

    private final HashMap<Integer, GameData> games = new HashMap<>();

    public GameData createGame(String gameName) throws ResponseException{
        int gameID = 1000 + games.size();
        GameData game = new GameData(null, null, gameID, gameName, new ChessGame());
        games.put(gameID, game);
        return game;
    }

    public GameData getGame(Integer gameID) throws ResponseException{
        if (games.containsKey(gameID)){
            return games.get(gameID);
        }
        throw new ResponseException(400, "No such game.");
    }

    public Collection<GameData> readGames() throws ResponseException{
        Collection<GameData> gameList = games.values();
        return gameList;
    }

    // Might need to edit to account for the wrong color being entered.
    public GameData updateGame(String userName, String playerColor, GameData unupdatedGame) throws ResponseException{
        games.remove(unupdatedGame.gameID());
        GameData updatedGame;
        if(playerColor == "BLACK") {
            updatedGame = new GameData(unupdatedGame.whiteUserName(), userName, unupdatedGame.gameID(), unupdatedGame.gameName(), unupdatedGame.chessGame());
            games.put(updatedGame.gameID(), updatedGame);
            return updatedGame;
        }
        if (playerColor == "WHITE") {
            updatedGame = new GameData(userName, unupdatedGame.blackUserName(), unupdatedGame.gameID(), unupdatedGame.gameName(), unupdatedGame.chessGame());
            games.put(updatedGame.gameID(), updatedGame);
            return updatedGame;
        }
        String err = playerColor + " is an invalid player color.";
        throw new ResponseException(400, err);
    }

    public void deleteGame(Integer gameID) throws ResponseException{
        games.remove(gameID);
    }

    public void clearGames() throws ResponseException{
        games.clear();
    }
}

