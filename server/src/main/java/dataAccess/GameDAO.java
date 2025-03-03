package dataAccess;

import model.GameData;
import java.util.Collection;
import exception.*;

public interface GameDAO {
    GameData createGame(String gameName) throws ResponseException;

    GameData getGame(Integer gameID) throws ResponseException;

    Collection<GameData> readGames() throws ResponseException;

    GameData updateGame(String userName, String playerColor, GameData unupdatedGame) throws ResponseException;

    void deleteGame(Integer gameID) throws ResponseException;

    void clearGames() throws ResponseException;

}