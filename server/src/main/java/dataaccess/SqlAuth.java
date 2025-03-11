package dataaccess;

import exception.ResponseException;
import model.AuthData;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlAuth implements AuthDAO{
    public AuthData createAuth(AuthData auth) throws ResponseException{
//        var statement = "INSERT INTO pet (name, type, json) VALUES (?, ?, ?)";
//        var json = new Gson().toJson(pet);
//        var id = executeUpdate(statement, pet.name(), pet.type(), json);
//        return new Pet(id, pet.name(), pet.type());
    }

    public AuthData getAuth(String authToken) throws ResponseException{
        return null;
    }

    public void deleteAuth(String authToken) throws ResponseException{
//        var statement = "DELETE FROM pet WHERE id=?";
//        executeUpdate(statement, id);
    }

    public void clearAuths() throws ResponseException{
//        var statement = "TRUNCATE pet";
//        executeUpdate(statement);
    }
}
