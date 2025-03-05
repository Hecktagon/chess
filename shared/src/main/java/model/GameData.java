package model;
import chess.ChessGame;
import com.google.gson.Gson;

public record GameData (String whiteUsername, String blackUsername, Integer gameID, String gameName, ChessGame chessGame) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
