package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import resreq.LoginRequest;
import resreq.LoginResponse;
import resreq.RegisterRequest;
import resreq.RegisterResponse;
import resreq.LogoutRequest;
import resreq.EmptyResponse;

import java.util.Objects;
import java.util.UUID;


public class UserService {
    AuthDAO authDAO;
    UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResponse register(RegisterRequest userRequest) throws ResponseException {
        UserData userCheck = userDAO.getUser(userRequest.username());

        // if user doesn't already exist, make new user and auth token
        if (userCheck == null){
            UserData userData = new UserData(userRequest.username(), userRequest.password(), userRequest.email());
            userData = userDAO.createUser(userData);
            if (userData.username() == null || userData.password() == null || userData.email() == null ||
            userData.username().isEmpty() || userData.password().isEmpty() || userData.email().isEmpty()){
                throw new ResponseException(400, "Error: bad request, missing register info");
            }

            UUID uuid = UUID.randomUUID();
            AuthData authData = authDAO.createAuth(new AuthData(uuid.toString(), userData.username()));
            return new RegisterResponse(authData.username(), authData.authToken());
        }
        else {
            throw new ResponseException(403, "Error: already taken");
        }
    }

    public LoginResponse login(LoginRequest loginRequest) throws ResponseException {
         UserData userData = userDAO.getUser(loginRequest.username());
         if(userData != null){
             if (!Objects.equals(userData.password(), loginRequest.password()) &&
                     !BCrypt.checkpw(loginRequest.password(), userData.password())){
                 throw new ResponseException(401, "Error: incorrect password");
             }
             String uuid = UUID.randomUUID().toString();
             authDAO.createAuth(new AuthData(uuid, loginRequest.username()));
             return new LoginResponse(loginRequest.username(), uuid);
         }
         throw new ResponseException(401, "Error: no such user" );
    }

    public EmptyResponse logout(LogoutRequest logoutRequest) throws ResponseException {
        AuthData auth = authDAO.getAuth(logoutRequest.authToken());
        if (auth != null){
            authDAO.deleteAuth(auth.authToken());
            return new EmptyResponse();
        }
        throw new ResponseException(401, "Error: logout failed, unauthorized");
    }
}
