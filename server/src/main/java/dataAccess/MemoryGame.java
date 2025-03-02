package dataAccess;

import model.GameData;

import java.util.Collection;
import java.util.HashSet;

public class MemoryGame implements GameDAO{
    private HashSet<GameData> games = new HashSet<>();

    public GameData createGame(GameData game) throws DataAccessException{
        games.add(game);
        return game;
    }

    public GameData getGame(GameData game) throws DataAccessException{
        if (games.contains(game)){
            return game;
        }
        throw new DataAccessException("No such game.");
    }

    public Collection<GameData> readGames() throws DataAccessException{
        return games;
    }

    // Might need to edit to account for the wrong color being entered.
    public GameData updateGame(String userName, String playerColor, GameData unupdatedGame) throws DataAccessException{
        games.remove(unupdatedGame);
        GameData updatedGame;
        if(playerColor == "BLACK") {
            updatedGame = new GameData(unupdatedGame.whiteUserName(), userName, unupdatedGame.gameID());
            return updatedGame;
        }
        if (playerColor == "WHITE") {
            updatedGame = new GameData(userName, unupdatedGame.blackUserName(), unupdatedGame.gameID());
            return updatedGame;
        }
        String err = playerColor + " is an invalid player color.";
        throw new DataAccessException(err);
    }

    public void deleteGame(GameData game) throws DataAccessException{
        games.remove(game);
    }

    public void clearGames() throws DataAccessException{
        games.clear();
    }
}

