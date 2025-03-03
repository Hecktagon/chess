package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import resReq.RegisterRequest;
import resReq.RegisterResponse;
import java.util.UUID;


public class UserService {
    AuthDAO authDAO;
    UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResponse register(RegisterRequest userReq) throws ResponseException {
        UserData userCheck = userDAO.getUser(userReq.username());

        // if user doesn't already exist, make new user and auth token
        if (userCheck == null){
            UserData userData = new UserData(userReq.username(), userReq.password(), userReq.email());
            userData = userDAO.createUser(userData);

            UUID uuid = UUID.randomUUID();
            AuthData authData = authDAO.createAuth(new AuthData(uuid.toString(), userData.username()));
            return new RegisterResponse(authData.username(), authData.authToken());
        }
        else {
            throw new ResponseException(403, "Error: already taken");
        }
    }
}
