package dataaccess;

import exception.ResponseException;
import model.UserData;

public interface UserDAO {
    UserData createUser(UserData user) throws ResponseException;

    UserData getUser(String userName) throws ResponseException;

    void clearUsers() throws ResponseException;

}