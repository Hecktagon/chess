package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import java.util.Collection;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlGame implements GameDAO {
    public GameData createGame(String gameName) throws ResponseException{
//        var statement = "INSERT INTO pet (name, type, json) VALUES (?, ?, ?)";
//        var json = new Gson().toJson(pet);
//        var id = executeUpdate(statement, pet.name(), pet.type(), json);
//        return new Pet(id, pet.name(), pet.type());
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
    }

    public GameData updateGame(String userName, ChessGame.TeamColor playerColor, Integer gameID) throws ResponseException{
        return null;
    }

    public void clearGames() throws ResponseException{
//        var statement = "TRUNCATE pet";
//        executeUpdate(statement);
    }

}
