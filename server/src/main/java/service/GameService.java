package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuth;
import dataaccess.MemoryGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import resreq.*;

import java.util.Vector;


public class GameService {
    AuthDAO authDAO;
    GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResponse listGames(ListGamesRequest listGamesRequest) throws ResponseException {
        AuthData auth = authDAO.getAuth(listGamesRequest.authToken());
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        return new ListGamesResponse(new Vector<>(gameDAO.readGames()));
    }

    public CreateGameResponse createGame(CreateGameRequest createGameRequest) throws ResponseException {
        AuthData auth = authDAO.getAuth(createGameRequest.authToken());
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        if (createGameRequest.gameName() == null || createGameRequest.gameName().isEmpty()){
            throw new ResponseException(400, "Error: bad request");
        }
        GameData createdGame = gameDAO.createGame(createGameRequest.gameName());
        return new CreateGameResponse(createdGame.gameID());
    }

    public EmptyResponse joinGame(JoinGameRequest joinGameRequest) throws ResponseException {
        AuthData auth = authDAO.getAuth(joinGameRequest.authToken());
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        GameData gameToJoin = gameDAO.getGame(joinGameRequest.gameID());
        if(gameToJoin == null) {
            throw new ResponseException(400,  "Error: bad request, no such game");
        }

        if (joinGameRequest.playerColor() == ChessGame.TeamColor.WHITE){
            if (gameToJoin.whiteUsername() != null){
                throw new ResponseException(403,  "Error: white already taken");
            }
            gameDAO.updateGame(new GameData(auth.username(), gameToJoin.blackUsername(),
                    gameToJoin.gameID(), gameToJoin.gameName(), gameToJoin.chessGame()));
            return new EmptyResponse();

        } else if (joinGameRequest.playerColor() == ChessGame.TeamColor.BLACK){
            if (gameToJoin.blackUsername() != null){
                throw new ResponseException(403,  "Error: black already taken");
            }
            gameDAO.updateGame(new GameData(gameToJoin.whiteUsername(), auth.username(),
                    gameToJoin.gameID(), gameToJoin.gameName(), gameToJoin.chessGame()));
            return new EmptyResponse();

        } else {
            throw new ResponseException(400, "Error: bad request, invalid player color");
        }
    }
}
