package dataAccess;

import model.UserData;

import java.util.Collection;
import java.util.HashSet;

public class MemoryUser implements UserDAO{
    private HashSet<UserData> users = new HashSet<>();

    public UserData createUser(UserData user) throws DataAccessException{
        users.add(user);
        return user;
    }

    public UserData getUser(UserData user) throws DataAccessException{
        if (users.contains(user)){
            return user;
        }
        return null;
    }

    public Collection<UserData> readUsers() throws DataAccessException{
        return users;
    }

    public void deleteUser(UserData user) throws DataAccessException{
        users.remove(user);
    }

    public void clearUsers() throws DataAccessException{
        users.clear();
    }
}

