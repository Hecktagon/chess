package dataaccess;

import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;

public class AuthSqlTests {

    static AuthDAO sqlAuthAuth;
    static UserDAO sqlAuthUser;
    static GameDAO sqlAuthGame;

    @BeforeAll
    public static void setup() throws ResponseException {
        sqlAuthAuth = new SqlAuth();
        sqlAuthUser = new SqlUser();
        sqlAuthGame = new SqlGame();
    }

    @BeforeEach
    public void clearer() throws ResponseException{
        sqlAuthAuth.clearAuths();
        sqlAuthGame.clearGames();
        sqlAuthUser.clearUsers();
    }

    @Test
    @DisplayName("Create Auth Success")
    public void createAuthPass() throws ResponseException {
        AuthData testAuth = new AuthData("cool_auth_token", "cool_user_name");
        AuthData createdAuth = sqlAuthAuth.createAuth(testAuth);
        Assertions.assertNotNull(createdAuth);
    }

    @Test
    @DisplayName("Create Auth Fail")
    public void createAuthFail() throws ResponseException {
        AuthData testerAuth = new AuthData(null, "cool_name");
        ResponseException exception  = Assertions.assertThrows(ResponseException.class, () ->
                sqlAuthAuth.createAuth(testerAuth)
        );
        Assertions.assertEquals(500, exception.statusCode());
    }

    @Test
    @DisplayName("Get Auth Success")
    public void getAuthPass() throws ResponseException {
        AuthData testingAuth = new AuthData("cool_token", "cool_name");
        AuthData createdAuth = sqlAuthAuth.createAuth(testingAuth);
        AuthData gotAuth = sqlAuthAuth.getAuth(createdAuth.authToken());
        Assertions.assertNotNull(gotAuth);
    }

    @Test
    @DisplayName("Get Auth Fail")
    public void getAuthFail() throws ResponseException {
        AuthData gotAuth = sqlAuthAuth.getAuth("no_such_auth");
        Assertions.assertNull(gotAuth);
    }

    @Test
    @DisplayName("Delete Auth Success")
    public void deleteAuthPass() throws ResponseException {
        AuthData testyAuth = new AuthData("coolish_token", "cooler_name");
        AuthData createdAuth = sqlAuthAuth.createAuth(testyAuth);
        sqlAuthAuth.deleteAuth(createdAuth.authToken());
        AuthData gottenAuth = sqlAuthAuth.getAuth(testyAuth.authToken());
        Assertions.assertNull(gottenAuth);

    }

    @Test
    @DisplayName("Delete Auth Fail")
    public void deleteAuthFail() throws ResponseException {
        sqlAuthAuth.deleteAuth("no_such_auth");
    }

    @Test
    @DisplayName("Clear Auths Success")
    public void clearAuthsPass() throws ResponseException {
        AuthData testaliciousAuth = new AuthData("lame_token", "lame_name");
        AuthData createdAuth = sqlAuthAuth.createAuth(testaliciousAuth);
        sqlAuthAuth.clearAuths();
        AuthData gitAuth = sqlAuthAuth.getAuth(testaliciousAuth.authToken());
        Assertions.assertNull(gitAuth);

    }
}
