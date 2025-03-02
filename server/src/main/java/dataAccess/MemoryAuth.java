package dataAccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashSet;

public class MemoryAuth implements AuthDAO{
    private HashSet<AuthData> auths = new HashSet<>();

    public AuthData createAuth(AuthData auth) throws DataAccessException{
        auths.add(auth);
        return auth;
    }

    public AuthData getAuth(AuthData auth) throws DataAccessException{
        if (auths.contains(auth)){
            return auth;
        }
        return null;
    }

    public Collection<AuthData> readAuths() throws DataAccessException{
        return auths;
    }

    public void deleteAuth(AuthData auth) throws DataAccessException{
        auths.remove(auth);
    }

    public void clearAuths() throws DataAccessException{
        auths.clear();
    }
}
