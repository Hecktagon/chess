package dataaccess;

import exception.ResponseException;
import model.AuthData;
import java.sql.*;
import static java.sql.Types.NULL;

public class SqlAuth implements AuthDAO{

    public SqlAuth() throws ResponseException {
        configureAuthDatabase();
    }

    public AuthData createAuth(AuthData auth) throws ResponseException{
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeAuthUpdate(statement, auth.authToken(), auth.username());
        return auth;
    }

    public AuthData getAuth(String authToken) throws ResponseException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);  // not sure if this is correct, don't really understand what's going on here.
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(400, String.format("Unauthorized: %s", e.getMessage()));
        }
        return null;
    }

    public void deleteAuth(String authToken) throws ResponseException{
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeAuthUpdate(statement, authToken);
    }

    public void clearAuths() throws ResponseException{
        var statement = "TRUNCATE auth";
        executeAuthUpdate(statement);
    }

    private final String[] createAuthStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    private void executeAuthUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var authParam = params[i];
                    if (authParam instanceof String p) {ps.setString(i + 1, p);}
                    else if (authParam == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("unable to update auth database: %s, %s", statement, e.getMessage()));
        }
    }

    private void configureAuthDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var authStatement : createAuthStatements) {
                try (var preparedStatement = conn.prepareStatement(authStatement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException authEx) {
            throw new ResponseException(500, String.format("Unable to configure auth database: %s", authEx.getMessage()));
        }
    }
}
