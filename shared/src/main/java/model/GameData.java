package model;
import com.google.gson.Gson;

public record GameData (String whiteUserName, String blackUserName, String gameID) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
