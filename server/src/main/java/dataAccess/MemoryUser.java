package dataAccess;

import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class MemoryUser implements UserDAO{
    private HashMap<String, UserData> users = new HashMap<>();

    public UserData createUser(UserData user) throws DataAccessException{
        users.put(user.userName(),user);
        return user;
    }

    public UserData getUser(String userName) throws DataAccessException{
        if (users.containsKey(userName)){
            return users.get(userName);
        }
        return null;
    }

    public Collection<UserData> readUsers() throws DataAccessException{
        return users.values();
    }

    public void deleteUser(String userName) throws DataAccessException{
        users.remove(userName);
    }

    public void clearUsers() throws DataAccessException{
        users.clear();
    }
}

