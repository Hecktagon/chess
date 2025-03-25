package service;

import chess.ChessGame;
import dataaccess.MemoryAuth;
import dataaccess.MemoryGame;
import dataaccess.MemoryUser;
import exception.exception.ResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import resreq.*;

public class GameServiceTests {

    MemoryAuth memoryAuth = new MemoryAuth();
    MemoryUser memoryUser = new MemoryUser();
    MemoryGame memoryGame = new MemoryGame();
    final UserService userService = new UserService(memoryAuth, memoryUser);
    final ClearService clearService = new ClearService(memoryAuth, memoryGame, memoryUser);
    final GameService gameService = new GameService(memoryAuth, memoryGame);

    private RegisterResponse registerUser(String username, String password, String email) throws ResponseException {
        return userService.register(new RegisterRequest(username, password, email));
    }

    private CreateGameResponse createAndJoinGame(String authToken, ChessGame.TeamColor teamColor) throws ResponseException {
        CreateGameResponse gameResponse = gameService.createGame(new CreateGameRequest(authToken, "CoolGame"));
        gameService.joinGame(new JoinGameRequest(teamColor, gameResponse.gameID(), authToken));
        return gameResponse;
    }

    @Test
    @DisplayName("Create Game Success")
    public void createGamePass() throws ResponseException {
        clearService.clearAll();
        System.out.print("TESTING: Create Game Success\n\n");
        RegisterResponse registered = registerUser("Bob", "boB", "bob@hotmail.com");

        CreateGameResponse actual = gameService.createGame(new CreateGameRequest(registered.authToken(), "CoolGame"));
        System.out.print("Response:\n");
        System.out.print(actual + "\n");
        Assertions.assertNotNull(actual);

        System.out.print("\nPASSED :)\n\n");

    }

    @Test
    @DisplayName("Create Game Fail: unauthorized")
    public void createGameFail401() throws ResponseException {
        clearService.clearAll();
        ResponseException exception  = Assertions.assertThrows(ResponseException.class, () ->
                gameService.createGame(new CreateGameRequest(null, "CoolGame"))
        );
        Assertions.assertEquals(401, exception.statusCode());
    }

    @Test
    @DisplayName("List Game Success")
    public void listGamePass() throws ResponseException {
        clearService.clearAll();
        System.out.print("TESTING: List Game Success\n\n");
        RegisterResponse registered = registerUser("Bob", "boB", "bob@hotmail.com");

        gameService.createGame(new CreateGameRequest(registered.authToken(), "CoolGame"));

        ListGamesResponse actual = gameService.listGames(new ListGamesRequest(registered.authToken()));
        System.out.print("Response:\n");
        System.out.print(actual + "\n");
        Assertions.assertNotNull(actual);

        System.out.print("\nPASSED :)\n\n");

    }

    @Test
    @DisplayName("List Games Fail: unauthorized")
    public void listGamesFail401() throws ResponseException {
        clearService.clearAll();
        ResponseException exception  = Assertions.assertThrows(ResponseException.class, () ->
                gameService.listGames(new ListGamesRequest(null))
        );
        Assertions.assertEquals(401, exception.statusCode());
    }

    @Test
    @DisplayName("Join Game Success")
    public void joinGamePass() throws ResponseException {
        clearService.clearAll();
        System.out.print("TESTING: Join Game Success\n\n");

        System.out.print("\nRegistering User:\n");
        RegisterResponse registered = registerUser("Bob", "boB", "bob@hotmail.com");
        System.out.print(registered + "\n");

        System.out.print("\nCreating and Joining Game:\n");
        CreateGameResponse gameResponse = createAndJoinGame(registered.authToken(), ChessGame.TeamColor.WHITE);
        System.out.print(gameResponse + "\n");

        ListGamesResponse listGames = gameService.listGames(new ListGamesRequest(registered.authToken()));
        System.out.print(listGames + "\n");

        System.out.print("\nRegistering User 2:\n");
        RegisterResponse registered2 = registerUser("Billy", "ylliB", "billy@hotmail.com");
        System.out.print(registered2 + "\n");

        System.out.print("\nUser 2 Attempts To Join Game:\n");
        gameService.joinGame(new JoinGameRequest(ChessGame.TeamColor.BLACK, gameResponse.gameID(), registered2.authToken()));

        ListGamesResponse listGamesFinal = gameService.listGames(new ListGamesRequest(registered2.authToken()));
        System.out.print(listGamesFinal + "\n");
        Assertions.assertNotNull(listGamesFinal);
    }

    @Test
    @DisplayName("Join Game Fail: already taken")
    public void joinGameFail403() throws ResponseException {
        clearService.clearAll();
        System.out.print("TESTING: Join Game Fail: already taken\n\n");

        System.out.print("\nRegistering User:\n");
        RegisterResponse registered = registerUser("Bob", "boB", "bob@hotmail.com");
        System.out.print(registered + "\n");

        System.out.print("\nCreating and Joining Game:\n");
        CreateGameResponse gameResponse = createAndJoinGame(registered.authToken(), ChessGame.TeamColor.WHITE);
        System.out.print(gameResponse + "\n");

        ListGamesResponse listGames = gameService.listGames(new ListGamesRequest(registered.authToken()));
        System.out.print(listGames + "\n");

        System.out.print("\nRegistering User 2:\n");
        RegisterResponse registered2 = registerUser("Billy", "ylliB", "billy@hotmail.com");
        System.out.print(registered2 + "\n");

        System.out.print("\nUser 2 Attempts To Join Game:\n");
        ResponseException exception = Assertions.assertThrows(ResponseException.class, () ->
                gameService.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, gameResponse.gameID(), registered2.authToken()))
        );
        Assertions.assertEquals(403, exception.statusCode());
    }
}