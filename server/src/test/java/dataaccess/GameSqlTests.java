package dataaccess;

import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;


public class GameSqlTests {

    static AuthDAO sqlGameAuth;
    static UserDAO sqlGameUser;
    static GameDAO sqlGameGame;

    @BeforeAll
    public static void setup() throws ResponseException{
        sqlGameAuth = new SqlAuth();
        sqlGameUser = new SqlUser();
        sqlGameGame = new SqlGame();
    }

    @BeforeEach
    public void clearer() throws ResponseException{
        sqlGameAuth.clearAuths();
        sqlGameGame.clearGames();
        sqlGameUser.clearUsers();
    }

    @Test
    @DisplayName("Create Game Success")
    public void createGamePass() throws ResponseException {
        GameData createdGame = sqlGameGame.createGame("coolGameName");
        Assertions.assertNotNull(createdGame);
        System.out.print("Passed! Result: \n");
        System.out.print(createdGame + "\n");
    }

    @Test
    @DisplayName("Create Game Fail")
    public void createGameFail() throws ResponseException {
        ResponseException exception  = Assertions.assertThrows(ResponseException.class, () ->
           sqlGameGame.createGame(null)
        );
        Assertions.assertEquals(500, exception.statusCode());
    }

    @Test
    @DisplayName("Get Game Success")
    public void getGamePass() throws ResponseException {
        GameData createdGame = sqlGameGame.createGame("coolGameName");
        GameData gotGame = sqlGameGame.getGame(1);
        Assertions.assertNotNull(gotGame);
        System.out.print("Passed! Result: \n");
        System.out.print(gotGame + "\n");
    }

    @Test
    @DisplayName("Get Game Fail")
    public void getGameFail() throws ResponseException {
        GameData createdGame = sqlGameGame.createGame("coolGameName");
        GameData gotGame = sqlGameGame.getGame(87263947);
        Assertions.assertNull(gotGame);
    }

    @Test
    @DisplayName("Read Games Success")
    public void readGamesPass() throws ResponseException {
        GameData createdGame = sqlGameGame.createGame("newGame1");
        GameData createdGame2 = sqlGameGame.createGame("newGame2");

        Collection<GameData> gameList = sqlGameGame.readGames();
        Assertions.assertEquals(2, gameList.size());
        System.out.print("Passed Read Game! Result: \n");
        System.out.print(gameList + "\n");

    }

    @Test
    @DisplayName("Read Games Fail")
    public void readGamesFail() throws ResponseException {
        Collection<GameData> gameList = sqlGameGame.readGames();
        Assertions.assertTrue(gameList.isEmpty());
    }

    @Test
    @DisplayName("Update Game Success")
    public void updateGamePass() throws ResponseException {
        GameData createdGame = sqlGameGame.createGame("newGame1");
        GameData update = new GameData(createdGame.whiteUsername(), "The_Black_Knight",
                createdGame.gameID(), createdGame.gameName(), createdGame.chessGame());
        GameData updatedGame = sqlGameGame.updateGame(update);

        Assertions.assertEquals(updatedGame.blackUsername(), update.blackUsername());
        System.out.print("Passed Update Game! Result: \n");
        System.out.print(updatedGame + "\n");
    }

    @Test
    @DisplayName("Update Game Fail")
    public void updateGameFail() throws ResponseException {
        GameData createdGame = sqlGameGame.createGame("newGame1");
        GameData badUpdate = new GameData(null, null,
                createdGame.gameID(), null, null);
        GameData updatedGame = sqlGameGame.updateGame(badUpdate);
        Assertions.assertEquals(badUpdate, updatedGame);
    }

    @Test
    @DisplayName("Clear Game Success")
    public void clearGamePass() throws ResponseException {
        sqlGameGame.createGame("DeleteMe!");
        sqlGameGame.clearGames();
        Assertions.assertTrue(sqlGameGame.readGames().isEmpty());
    }
}