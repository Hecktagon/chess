package dataaccess;

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

    @Test
    @DisplayName("Clear Pass")
    public void clearPass() throws ResponseException {

    }

    @Test
    @DisplayName("Create User Success")
    public void createUserPass() throws ResponseException {

    }

    @Test
    @DisplayName("Create User Fail")
    public void createUserFail() throws ResponseException {

    }

    @Test
    @DisplayName("Get User Success")
    public void getUserPass() throws ResponseException {

    }

    @Test
    @DisplayName("Get User Fail")
    public void getUserFail() throws ResponseException {

    }
}
