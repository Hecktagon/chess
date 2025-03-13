package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;
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

    @BeforeEach
    public void clearer() throws ResponseException{
        sqlAuth.clearAuths();
        sqlGame.clearGames();
        sqlUser.clearUsers();
    }

    @Test
    @DisplayName("Create Auth Success")
    public void createAuthPass() throws ResponseException {
        AuthData testAuth = new AuthData("cool_auth_token", "cool_user_name");
        AuthData createdAuth = sqlAuth.createAuth(testAuth);
        Assertions.assertNotNull(createdAuth);
    }

    @Test
    @DisplayName("Create Auth Fail")
    public void createAuthFail() throws ResponseException {
        AuthData testerAuth = new AuthData(null, "cool_name");
        ResponseException exception  = Assertions.assertThrows(ResponseException.class, () ->
                sqlAuth.createAuth(testerAuth)
        );
        Assertions.assertEquals(500, exception.statusCode());
    }

    @Test
    @DisplayName("Get Auth Success")
    public void getAuthPass() throws ResponseException {
        AuthData testingAuth = new AuthData("cool_token", "cool_name");
        AuthData createdAuth = sqlAuth.createAuth(testingAuth);
        AuthData gotAuth = sqlAuth.getAuth(createdAuth.authToken());
        Assertions.assertNotNull(gotAuth);
    }

    @Test
    @DisplayName("Get Auth Fail")
    public void getAuthFail() throws ResponseException {
        AuthData gotAuth = sqlAuth.getAuth("no_such_auth");
        Assertions.assertNull(gotAuth);
    }

    @Test
    @DisplayName("Delete Auth Success")
    public void deleteAuthPass() throws ResponseException {
        AuthData testyAuth = new AuthData("coolish_token", "cooler_name");
        AuthData createdAuth = sqlAuth.createAuth(testyAuth);
        sqlAuth.deleteAuth(createdAuth.authToken());
        AuthData gottenAuth = sqlAuth.getAuth(testyAuth.authToken());
        Assertions.assertNull(gottenAuth);

    }

    @Test
    @DisplayName("Delete Auth Fail")
    public void deleteAuthFail() throws ResponseException {
        sqlAuth.deleteAuth("no_such_auth");
    }

    @Test
    @DisplayName("Clear Auths Success")
    public void clearAuthsPass() throws ResponseException {
        AuthData testaliciousAuth = new AuthData("lame_token", "lame_name");
        AuthData createdAuth = sqlAuth.createAuth(testaliciousAuth);
        sqlAuth.clearAuths();
        AuthData gitAuth = sqlAuth.getAuth(testaliciousAuth.authToken());
        Assertions.assertNull(gitAuth);

    }
}
