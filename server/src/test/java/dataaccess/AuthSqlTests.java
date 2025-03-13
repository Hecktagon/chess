package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import resreq.*;

public class AuthSqlTests {

    static AuthDAO sqlAuth;
    static UserDAO sqlUser;
    static GameDAO sqlGame;

    @BeforeAll
    public static void setup() throws ResponseException {
        sqlAuth = new SqlAuth();
        sqlUser = new SqlUser();
        sqlGame = new SqlGame();
    }

    @Test
    @DisplayName("Create Auth Success")
    public void createAuthPass() throws ResponseException {

    }

    @Test
    @DisplayName("Create Auth Fail")
    public void createAuthFail() throws ResponseException {

    }

    @Test
    @DisplayName("Get Auth Success")
    public void getAuthPass() throws ResponseException {

    }

    @Test
    @DisplayName("Get Auth Fail")
    public void getAuthFail() throws ResponseException {

    }

    @Test
    @DisplayName("Delete Auth Success")
    public void deleteAuthPass() throws ResponseException {

    }

    @Test
    @DisplayName("Delete Auth Fail")
    public void deleteAuthFail() throws ResponseException {

    }

    @Test
    @DisplayName("Clear Auths Success")
    public void clearAuthsPass() throws ResponseException {

    }
}
