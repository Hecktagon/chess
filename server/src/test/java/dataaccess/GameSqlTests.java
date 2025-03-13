package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import resreq.*;


public class GameSqlTests {

    static AuthDAO sqlAuth;
    static UserDAO sqlUser;
    static GameDAO sqlGame;

    @BeforeAll
    public static void setup() throws ResponseException{
        sqlAuth = new SqlAuth();
        sqlUser = new SqlUser();
        sqlGame = new SqlGame();
    }

    @Test
    @DisplayName("Create Game Success")
    public void createGamePass() throws ResponseException {

    }

    @Test
    @DisplayName("Create Game Fail")
    public void createGameFail() throws ResponseException {

    }

    @Test
    @DisplayName("Get Game Success")
    public void getGamePass() throws ResponseException {

    }

    @Test
    @DisplayName("Get Game Fail")
    public void getGameFail() throws ResponseException {

    }

    @Test
    @DisplayName("Read Games Success")
    public void readGamesPass() throws ResponseException {

    }

    @Test
    @DisplayName("Read Games Fail")
    public void readGamesFail() throws ResponseException {

    }

    @Test
    @DisplayName("Update Game Success")
    public void updateGamePass() throws ResponseException {

    }

    @Test
    @DisplayName("Update Game Fail")
    public void updateGameFail() throws ResponseException {

    }

    @Test
    @DisplayName("Clear Game Success")
    public void clearGamePass() throws ResponseException {

    }
}