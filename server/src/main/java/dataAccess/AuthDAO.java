package dataAccess;

import model.AuthData;
import exception.*;
import java.util.Collection;

public interface AuthDAO {
    AuthData createAuth(AuthData auth) throws ResponseException;

    AuthData getAuth(AuthData auth) throws ResponseException;

    Collection<AuthData> readAuths() throws ResponseException;

    void deleteAuth(AuthData auth) throws ResponseException;

    void clearAuths() throws ResponseException;
}
