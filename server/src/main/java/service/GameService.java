package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import resReq.ListGamesRequest;
import resReq.ListGamesResponse;


public class GameService {
    AuthDAO authDAO;
    GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResponse listGames(ListGamesRequest listGamesRequest){

    }
}
