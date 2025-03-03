package dataAccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MemoryGame implements GameDAO{

    private HashMap<Integer, GameData> games = new HashMap<>();

    public GameData createGame(String gameName) throws DataAccessException{
        int gameID = 100000 + games.size();
        GameData game = new GameData(null, null, gameID, gameName);
        games.put(gameID, game);
        return game;
    }

    public GameData getGame(Integer gameID) throws DataAccessException{
        if (games.containsKey(gameID)){
            return games.get(gameID);
        }
        throw new DataAccessException("No such game.");
    }

    public Collection<GameData> readGames() throws DataAccessException{
        return games.values();
    }

    // Might need to edit to account for the wrong color being entered.
    public GameData updateGame(String userName, String playerColor, GameData unupdatedGame) throws DataAccessException{
        games.remove(unupdatedGame);
        GameData updatedGame;
        if(playerColor == "BLACK") {
            updatedGame = new GameData(unupdatedGame.whiteUserName(), userName, unupdatedGame.gameID(), unupdatedGame.gameName());
            return updatedGame;
        }
        if (playerColor == "WHITE") {
            updatedGame = new GameData(userName, unupdatedGame.blackUserName(), unupdatedGame.gameID(), unupdatedGame.gameName());
            return updatedGame;
        }
        String err = playerColor + " is an invalid player color.";
        throw new DataAccessException(err);
    }

    public void deleteGame(Integer gameID) throws DataAccessException{
        games.remove(gameID);
    }

    public void clearGames() throws DataAccessException{
        games.clear();
    }
}

