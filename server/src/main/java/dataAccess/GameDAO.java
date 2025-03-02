package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData createGame(GameData game) throws DataAccessException;

    GameData getGame(GameData game) throws DataAccessException;

    Collection<GameData> readGames() throws DataAccessException;

    GameData updateGame(String userName, String playerColor, GameData unupdatedGame) throws DataAccessException;

    void deleteGame(GameData game) throws DataAccessException;

    void clearGames() throws DataAccessException;

}