package service;
import exception.ResponseException;
import dataAccess.*;

public class ClearService {
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;
//    ClearService(AuthDAO auth, GameDAO game, UserDAO user){
//
//    }

    public ClearService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public void clearAll() throws ResponseException {
        this.authDAO.clearAuths();
        this.gameDAO.clearGames();
        this.userDAO.clearUsers();
    }
}
