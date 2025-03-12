package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import exception.*;

public interface GameDAO {
    GameData createGame(String gameName) throws ResponseException;

    Collection<GameData> readGames() throws ResponseException;

    GameData getGame(Integer gameID) throws ResponseException;

    GameData updateGame(GameData gameData) throws ResponseException;

    void clearGames() throws ResponseException;

}