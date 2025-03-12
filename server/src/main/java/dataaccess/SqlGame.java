package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import java.util.Collection;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlGame implements GameDAO {

    public SqlGame() throws ResponseException {
        configureGameDatabase();
    }

    public GameData createGame(String gameName) throws ResponseException{
//        var statement = "INSERT INTO pet (name, type, json) VALUES (?, ?, ?)";
//        var json = new Gson().toJson(pet);
//        var id = executeUpdate(statement, pet.name(), pet.type(), json);
//        return new Pet(id, pet.name(), pet.type());
        return null;
    }

    public Collection<GameData> readGames() throws ResponseException{
//        var result = new ArrayList<Pet>();
//        try (var conn = DatabaseManager.getConnection()) {
//            var statement = "SELECT id, json FROM pet";
//            try (var ps = conn.prepareStatement(statement)) {
//                try (var rs = ps.executeQuery()) {
//                    while (rs.next()) {
//                        result.add(readPet(rs));
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
//        }
//        return result;
        return null;
    }

    public GameData updateGame(String userName, ChessGame.TeamColor playerColor, Integer gameID) throws ResponseException{
        return null;
    }

    public void clearGames() throws ResponseException{
//        var statement = "TRUNCATE pet";
//        executeUpdate(statement);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `gameName` varchar(256) NOT NULL,
              `chessGame` JSON NOT NULL,
              PRIMARY KEY (`gameID`),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureGameDatabase() throws ResponseException {
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
