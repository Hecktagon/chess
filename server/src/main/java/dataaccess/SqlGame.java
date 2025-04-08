package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlGame implements GameDAO {

    public SqlGame() throws ResponseException {
        configureGameDatabase();
    }

    public GameData createGame(String gameName) throws ResponseException {
        var statement = "INSERT INTO game (gameName, chessGame) VALUES (?, ?)";
        ChessGame chessGame = new ChessGame();
        var gameJson = new Gson().toJson(chessGame);
        var id = executeUpdate(statement, gameName, gameJson);
        return new GameData(null, null, id, gameName, chessGame);
    }

    public Collection<GameData> readGames() throws ResponseException{
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    public GameData getGame(Integer gameID) throws ResponseException{
        if(gameID == null){
            throw new ResponseException(400, "Error: Invalid gameID");
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);  // not sure if this is correct, don't really understand what's going on here.
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public GameData updateGame(GameData gameData) throws ResponseException{
        var statement = """
        UPDATE game
        SET whiteUsername = ?, blackUsername = ?, chessGame = ?
        WHERE gameID = ?
        """;
        executeUpdate(statement, gameData.whiteUsername(), gameData.blackUsername(), new Gson().toJson(gameData.chessGame()) ,gameData.gameID());
        return gameData;
    }

    public void clearGames() throws ResponseException{
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var white = rs.getString("whiteUsername");
        var black = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var unSerializedGame = rs.getString("chessGame");
        var chessGame = new Gson().fromJson(unSerializedGame, ChessGame.class);
        return new GameData(white, black, gameID, gameName, chessGame);
    }


    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    }
                    else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `chessGame` JSON NOT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureGameDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(createStatements[0])) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure game database: %s", ex.getMessage()));
        }
    }

}
