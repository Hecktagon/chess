package dataaccess;

import model.UserData;
import exception.exception.ResponseException;
import org.junit.jupiter.api.*;

public class UserSqlTests {

    static AuthDAO sqlUserAuth;
    static UserDAO sqlUserUser;
    static GameDAO sqlUserGame;

    @BeforeAll
    public static void setupUser() throws ResponseException {
        sqlUserAuth = new SqlAuth();
        sqlUserUser = new SqlUser();
        sqlUserGame = new SqlGame();
    }

    @BeforeEach
    public void clearer() throws ResponseException{
        sqlUserAuth.clearAuths();
        sqlUserGame.clearGames();
        sqlUserUser.clearUsers();
    }

    @Test
    @DisplayName("Create User Success")
    public void createUserPass() throws ResponseException {
        UserData testUser = new UserData("cool_user_name", "password123", "email@example.com");
        UserData createdUser = sqlUserUser.createUser(testUser);
        Assertions.assertNotNull(createdUser);
    }

    @Test
    @DisplayName("Create User Fail")
    public void createUserFail() throws ResponseException {
        UserData testerUser = new UserData(null, "password123", "email@example.com");
        ResponseException exception = Assertions.assertThrows(ResponseException.class, () ->
                sqlUserUser.createUser(testerUser)
        );
        Assertions.assertEquals(400, exception.statusCode());
    }

    @Test
    @DisplayName("Get User Success")
    public void getUserPass() throws ResponseException {
        UserData testingUser = new UserData("cool_user_name", "password123", "email@example.com");
        UserData createdUser = sqlUserUser.createUser(testingUser);
        UserData gotUser = sqlUserUser.getUser(createdUser.username());
        Assertions.assertNotNull(gotUser);
    }

    @Test
    @DisplayName("Get User Fail")
    public void getUserFail() throws ResponseException {
        UserData gotUser = sqlUserUser.getUser("no_such_user");
        Assertions.assertNull(gotUser);
    }

    @Test
    @DisplayName("Clear Pass")
    public void clearPass() throws ResponseException {
        UserData testUser = new UserData("cool_user_name", "password123", "email@example.com");
        UserData createdUser = sqlUserUser.createUser(testUser);
        sqlUserUser.clearUsers();
        UserData gotUser = sqlUserUser.getUser(testUser.username());
        Assertions.assertNull(gotUser);
    }
}