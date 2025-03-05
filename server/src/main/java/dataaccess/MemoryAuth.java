package dataaccess;

import model.AuthData;
import exception.*;
import java.util.Collection;
import java.util.HashMap;

public class MemoryAuth implements AuthDAO{
    private final HashMap<String, AuthData> auths = new HashMap<>();

    public AuthData createAuth(AuthData auth) throws ResponseException{
        auths.put(auth.authToken(), auth);
        return auth;
    }

    public AuthData getAuth(String authToken) throws ResponseException{
        if (auths.containsKey(authToken)){
            return auths.get(authToken);
        }
        return null;
    }

    public Collection<AuthData> readAuths() throws ResponseException{
        return auths.values();
    }

    public void deleteAuth(String authToken) throws ResponseException{
        auths.remove(authToken);
    }

    public void clearAuths() throws ResponseException{
        auths.clear();
    }
}
