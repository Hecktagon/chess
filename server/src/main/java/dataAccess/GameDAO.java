package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException;

    GameData getGame(Integer gameID) throws DataAccessException;

    Collection<GameData> readGames() throws DataAccessException;

    GameData updateGame(String userName, String playerColor, GameData unupdatedGame) throws DataAccessException;

    void deleteGame(Integer gameID) throws DataAccessException;

    void clearGames() throws DataAccessException;

}