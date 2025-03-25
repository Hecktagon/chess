package dataaccess;

import exception.exception.ResponseException;
import model.UserData;

import java.util.HashMap;

public class MemoryUser implements UserDAO{
    private final HashMap<String, UserData> users = new HashMap<>();

    public UserData createUser(UserData user) throws ResponseException {
        users.put(user.username(),user);
        return user;
    }

    public UserData getUser(String username) throws ResponseException{
        if (users.containsKey(username)){
            return users.get(username);
        }
        return null;
    }

    public void clearUsers() throws ResponseException{
        users.clear();
    }
}

