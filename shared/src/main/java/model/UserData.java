package model;
import com.google.gson.Gson;

public record UserData (String userName, String password, String email) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
