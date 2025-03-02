package dataAccess;

import model.AuthData;

import java.util.Collection;

public interface AuthDAO {
    AuthData createAuth(AuthData auth) throws DataAccessException;

    AuthData getAuth(AuthData auth) throws DataAccessException;

    Collection<AuthData> readAuths() throws DataAccessException;

    void deleteAuth(AuthData auth) throws DataAccessException;

    void clearAuths() throws DataAccessException;
}
