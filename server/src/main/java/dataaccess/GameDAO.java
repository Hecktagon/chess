package dataaccess;

import exception.ResponseException;
import model.GameData;
import java.util.Collection;

public interface GameDAO {
    GameData createGame(String gameName) throws ResponseException;

    Collection<GameData> readGames() throws ResponseException;

    GameData getGame(Integer gameID) throws ResponseException;

    GameData updateGame(GameData gameData) throws ResponseException;

    void clearGames() throws ResponseException;

}