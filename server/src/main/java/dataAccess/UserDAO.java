package dataAccess;

import model.UserData;

import java.util.Collection;

public interface UserDAO {
    UserData createUser(UserData user) throws DataAccessException;

    UserData getUser(UserData user) throws DataAccessException;

    Collection<UserData> readUsers() throws DataAccessException;

    void deleteUser(UserData user) throws DataAccessException;

    void clearUsers() throws DataAccessException;

}