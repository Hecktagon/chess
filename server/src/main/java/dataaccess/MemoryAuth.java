package dataaccess;

import exception.ResponseException;
import model.AuthData;

import java.util.HashMap;

public class MemoryAuth implements AuthDAO{
    private final HashMap<String, AuthData> auths = new HashMap<>();

    public AuthData createAuth(AuthData auth) throws ResponseException {
        auths.put(auth.authToken(), auth);
        return auth;
    }

    public AuthData getAuth(String authToken) throws ResponseException{
        if (auths.containsKey(authToken)){
            return auths.get(authToken);
        }
        return null;
    }

    public void deleteAuth(String authToken) throws ResponseException{
        auths.remove(authToken);
    }

    public void clearAuths() throws ResponseException{
        auths.clear();
    }
}
