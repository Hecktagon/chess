package dataAccess;

import model.AuthData;
import exception.*;
import java.util.Collection;
import java.util.HashSet;

public class MemoryAuth implements AuthDAO{
    private final HashSet<AuthData> auths = new HashSet<>();

    public AuthData createAuth(AuthData auth) throws ResponseException{
        auths.add(auth);
        return auth;
    }

    public AuthData getAuth(AuthData auth) throws ResponseException{
        if (auths.contains(auth)){
            return auth;
        }
        return null;
    }

    public Collection<AuthData> readAuths() throws ResponseException{
        return auths;
    }

    public void deleteAuth(AuthData auth) throws ResponseException{
        auths.remove(auth);
    }

    public void clearAuths() throws ResponseException{
        auths.clear();
    }
}
