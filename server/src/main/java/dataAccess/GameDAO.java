package dataAccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import exception.*;

public interface GameDAO {
    GameData createGame(String gameName) throws ResponseException;

    GameData getGame(Integer gameID) throws ResponseException;

    Collection<GameData> readGames() throws ResponseException;

    GameData updateGame(String userName, ChessGame.TeamColor playerColor, Integer gameID) throws ResponseException;

    void deleteGame(Integer gameID) throws ResponseException;

    void clearGames() throws ResponseException;

}