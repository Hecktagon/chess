package dataaccess;

import exception.ResponseException;
import model.AuthData;

public class SqlAuth implements AuthDAO{
    public AuthData createAuth(AuthData auth) throws ResponseException{
        return null;
    }

    public AuthData getAuth(String authToken) throws ResponseException{
        return null;
    }

    public void deleteAuth(String authToken) throws ResponseException{

    }

    public void clearAuths() throws ResponseException{

    }
}
