package dataAccess;

import chess.ChessGame;
import model.GameData;
import exception.*;

import java.util.*;

public class MemoryGame implements GameDAO{

    private final HashMap<Integer, GameData> games = new HashMap<>();

    public GameData createGame(String gameName) throws ResponseException{
        int gameID = 1000 + games.size();
        GameData game = new GameData(null, null, gameID, gameName, new ChessGame());
        games.put(gameID, game);
        return game;
    }

    public GameData getGame(Integer gameID) throws ResponseException{
        if (games.containsKey(gameID)){
            return games.get(gameID);
        }
        throw new ResponseException(400, "No such game.");
    }

    public Collection<GameData> readGames() throws ResponseException{
        Collection<GameData> gameList = games.values();
        return gameList;
    }

    public GameData updateGame(String userName, String playerColor, Integer gameID) throws ResponseException{
        System.out.print(playerColor);
        GameData unupdatedGame = games.get(gameID);
        if(unupdatedGame == null){

        }
        GameData updatedGame;
        if(Objects.equals(playerColor, "BLACK") && unupdatedGame.blackUserName() == null) {
            updatedGame = new GameData(unupdatedGame.whiteUserName(), userName, unupdatedGame.gameID(), unupdatedGame.gameName(), unupdatedGame.chessGame());
            games.remove(gameID);
            games.put(updatedGame.gameID(), updatedGame);
            return updatedGame;
        }
        if (Objects.equals(playerColor, "WHITE") && unupdatedGame.whiteUserName() == null) {
            updatedGame = new GameData(userName, unupdatedGame.blackUserName(), unupdatedGame.gameID(), unupdatedGame.gameName(), unupdatedGame.chessGame());
            games.remove(gameID);
            games.put(updatedGame.gameID(), updatedGame);
            return updatedGame;
        }
        if (!Objects.equals(playerColor, "WHITE") && !Objects.equals(playerColor, "BLACK")) {

            throw new ResponseException(400, "Error: bad request");
        }
        throw new ResponseException(403, "Error: already taken");
    }

    public void deleteGame(Integer gameID) throws ResponseException{
        games.remove(gameID);
    }

    public void clearGames() throws ResponseException{
        games.clear();
    }
}

