package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import resReq.CreateGameRequest;
import resReq.CreateGameResponse;
import resReq.ListGamesRequest;
import resReq.ListGamesResponse;

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
}
