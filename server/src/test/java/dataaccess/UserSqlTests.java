package dataaccess;

import model.UserData;
import service.*;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import resreq.*;

public class UserSqlTests {

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
    @DisplayName("Create User Success")
    public void createUserPass() throws ResponseException {
        UserData testUser = new UserData("cool_user_name", "password123", "email@example.com");
        UserData createdUser = sqlUser.createUser(testUser);
        Assertions.assertNotNull(createdUser);
    }

    @Test
    @DisplayName("Create User Fail")
    public void createUserFail() throws ResponseException {
        UserData testerUser = new UserData(null, "password123", "email@example.com");
        ResponseException exception = Assertions.assertThrows(ResponseException.class, () ->
                sqlUser.createUser(testerUser)
        );
        Assertions.assertEquals(400, exception.statusCode());
    }

    @Test
    @DisplayName("Get User Success")
    public void getUserPass() throws ResponseException {
        UserData testingUser = new UserData("cool_user_name", "password123", "email@example.com");
        UserData createdUser = sqlUser.createUser(testingUser);
        UserData gotUser = sqlUser.getUser(createdUser.username());
        Assertions.assertNotNull(gotUser);
    }

    @Test
    @DisplayName("Get User Fail")
    public void getUserFail() throws ResponseException {
        UserData gotUser = sqlUser.getUser("no_such_user");
        Assertions.assertNull(gotUser);
    }

    @Test
    @DisplayName("Clear Pass")
    public void clearPass() throws ResponseException {
        UserData testUser = new UserData("cool_user_name", "password123", "email@example.com");
        UserData createdUser = sqlUser.createUser(testUser);
        sqlUser.clearUsers();
        UserData gotUser = sqlUser.getUser(testUser.username());
        Assertions.assertNull(gotUser);
    }
}