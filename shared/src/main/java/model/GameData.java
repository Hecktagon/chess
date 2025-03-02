package model;
import com.google.gson.Gson;

public record GameData (String p1UserName, String p2UserName, String gameID) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
