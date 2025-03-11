package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

import java.util.Collection;

public class SqlGame implements GameDAO {
    public GameData createGame(String gameName) throws ResponseException{
        return null;
    }

    public Collection<GameData> readGames() throws ResponseException{
        return null;
    }

    public GameData updateGame(String userName, ChessGame.TeamColor playerColor, Integer gameID) throws ResponseException{
        return null;
    }

    public void clearGames() throws ResponseException{
        return;
    }

}
