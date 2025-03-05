package dataaccess;

import model.AuthData;
import exception.*;
import java.util.Collection;

public interface AuthDAO {
    AuthData createAuth(AuthData auth) throws ResponseException;

    AuthData getAuth(String authToken) throws ResponseException;

    void deleteAuth(String authToken) throws ResponseException;

    void clearAuths() throws ResponseException;
}
