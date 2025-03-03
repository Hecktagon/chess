package model;
import com.google.gson.Gson;

public record GameData (String whiteUserName, String blackUserName, Integer gameID, String gameName) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
