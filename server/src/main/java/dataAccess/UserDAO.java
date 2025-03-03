package dataAccess;

import model.UserData;

import java.util.Collection;

public interface UserDAO {
    public UserData createUser(UserData user) throws DataAccessException;

    public UserData getUser(String userName) throws DataAccessException;

    Collection<UserData> readUsers() throws DataAccessException;

    void deleteUser(String userName) throws DataAccessException;

    void clearUsers() throws DataAccessException;

}