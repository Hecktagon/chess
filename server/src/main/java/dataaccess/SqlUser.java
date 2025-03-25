package dataaccess;

import exception.ResponseException;
import model.UserData;
import java.sql.*;

import static java.sql.Types.NULL;
import org.mindrot.jbcrypt.BCrypt;

public class SqlUser implements UserDAO{

    public SqlUser() throws ResponseException {
        configureUserDatabase();
    }

    private String passHasher(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public UserData createUser(UserData user) throws ResponseException{
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String hashedPass = (user.password() == null) ? null :  passHasher(user.password());
        try {
            executeUpdate(statement, user.username(), hashedPass, user.email());
        } catch (Exception e) {
            throw new ResponseException(400, "Error: bad request, invalid username, password, or email\n" + e.getMessage());
        }
        return new UserData(user.username(), hashedPass, user.email());
    }

    public UserData getUser(String userName) throws ResponseException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM user WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, userName);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void clearUsers() throws ResponseException{
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    private final String[] createUserStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) {ps.setString(i + 1, p);}
                    else if (param == null) {ps.setNull(i + 1, NULL);}
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private void configureUserDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var userStatement : createUserStatements) {
                try (var preparedStatement = conn.prepareStatement(userStatement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure user database: %s", ex.getMessage()));
        }
    }

}
