package dataaccess;

import model.UserData;
import exception.*;
import java.util.Collection;

public interface UserDAO {
    UserData createUser(UserData user) throws ResponseException;

    UserData getUser(String userName) throws ResponseException;

    void clearUsers() throws ResponseException;

}