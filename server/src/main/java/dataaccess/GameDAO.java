package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import exception.*;

public interface GameDAO {
    GameData createGame(String gameName) throws ResponseException;

    Collection<GameData> readGames() throws ResponseException;

    GameData updateGame(String userName, ChessGame.TeamColor playerColor, Integer gameID) throws ResponseException;

    void clearGames() throws ResponseException;

}