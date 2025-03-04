package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;

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
