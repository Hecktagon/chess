package dataaccess;

import exception.ResponseException;
import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(AuthData auth) throws ResponseException;

    AuthData getAuth(String authToken) throws ResponseException;

    void deleteAuth(String authToken) throws ResponseException;

    void clearAuths() throws ResponseException;
}
