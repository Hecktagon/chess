package dataaccess;

import model.UserData;
import exception.*;
import java.util.Collection;

public interface UserDAO {
    public UserData createUser(UserData user) throws ResponseException;

    public UserData getUser(String userName) throws ResponseException;

    void deleteUser(String userName) throws ResponseException;

    void clearUsers() throws ResponseException;

}