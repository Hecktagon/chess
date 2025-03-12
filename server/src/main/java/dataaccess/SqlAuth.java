package dataaccess;

import exception.ResponseException;
import model.AuthData;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlAuth implements AuthDAO{

    public SqlAuth() throws ResponseException {
        configureAuthDatabase();
    }

    public AuthData createAuth(AuthData auth) throws ResponseException{
//        var statement = "INSERT INTO pet (name, type, json) VALUES (?, ?, ?)";
//        var json = new Gson().toJson(pet);
//        var id = executeUpdate(statement, pet.name(), pet.type(), json);
//        return new Pet(id, pet.name(), pet.type());
        return null;
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

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureAuthDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
