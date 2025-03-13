package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;
import resreq.*;

import java.util.Collection;


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

    @BeforeEach
    public void clearer() throws ResponseException{
        sqlAuth.clearAuths();
        sqlGame.clearGames();
        sqlUser.clearUsers();
    }

    @Test
    @DisplayName("Create Game Success")
    public void createGamePass() throws ResponseException {
        GameData createdGame = sqlGame.createGame("coolGameName");
        Assertions.assertNotNull(createdGame);
        System.out.print("Passed! Result: \n");
        System.out.print(createdGame + "\n");
    }

    @Test
    @DisplayName("Create Game Fail")
    public void createGameFail() throws ResponseException {
        ResponseException exception  = Assertions.assertThrows(ResponseException.class, () ->
           sqlGame.createGame(null)
        );
        Assertions.assertEquals(500, exception.statusCode());
    }

    @Test
    @DisplayName("Get Game Success")
    public void getGamePass() throws ResponseException {
        GameData createdGame = sqlGame.createGame("coolGameName");
        GameData gotGame = sqlGame.getGame(1);
        Assertions.assertNotNull(gotGame);
        System.out.print("Passed! Result: \n");
        System.out.print(gotGame + "\n");
    }

    @Test
    @DisplayName("Get Game Fail")
    public void getGameFail() throws ResponseException {
        GameData createdGame = sqlGame.createGame("coolGameName");
        GameData gotGame = sqlGame.getGame(87263947);
        Assertions.assertNull(gotGame);
    }

    @Test
    @DisplayName("Read Games Success")
    public void readGamesPass() throws ResponseException {
        GameData createdGame = sqlGame.createGame("newGame1");
        GameData createdGame2 = sqlGame.createGame("newGame2");

        Collection<GameData> gameList = sqlGame.readGames();
        Assertions.assertEquals(2, gameList.size());
        System.out.print("Passed Read Game! Result: \n");
        System.out.print(gameList + "\n");

    }

    @Test
    @DisplayName("Read Games Fail")
    public void readGamesFail() throws ResponseException {
        Collection<GameData> gameList = sqlGame.readGames();
        Assertions.assertTrue(gameList.isEmpty());
    }

    @Test
    @DisplayName("Update Game Success")
    public void updateGamePass() throws ResponseException {
        GameData createdGame = sqlGame.createGame("newGame1");
        GameData update = new GameData(createdGame.whiteUsername(), "The_Black_Knight",
                createdGame.gameID(), createdGame.gameName(), createdGame.chessGame());
        GameData updatedGame = sqlGame.updateGame(update);

        Assertions.assertEquals(updatedGame.blackUsername(), update.blackUsername());
        System.out.print("Passed Update Game! Result: \n");
        System.out.print(updatedGame + "\n");
    }

    @Test
    @DisplayName("Update Game Fail")
    public void updateGameFail() throws ResponseException {
        GameData createdGame = sqlGame.createGame("newGame1");
        GameData badUpdate = new GameData(null, null,
                createdGame.gameID(), null, null);
        GameData updatedGame = sqlGame.updateGame(badUpdate);
        Assertions.assertEquals(badUpdate, updatedGame);
    }

    @Test
    @DisplayName("Clear Game Success")
    public void clearGamePass() throws ResponseException {
        sqlGame.createGame("DeleteMe!");
        sqlGame.clearGames();
        Assertions.assertTrue(sqlGame.readGames().isEmpty());
    }
}