package model;
import chess.ChessGame;
import com.google.gson.Gson;

public record GameData (String whiteUserName, String blackUserName, Integer gameID, String gameName, ChessGame chessGame) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
