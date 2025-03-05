package service;

import dataAccess.MemoryAuth;
import dataAccess.MemoryGame;
import dataAccess.MemoryUser;
import exception.ResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import resReq.*;
import service.GameService;
import service.UserService;
import service.ClearService;

public class GameServiceTests {

    MemoryAuth memoryAuth = new MemoryAuth();
    MemoryUser memoryUser = new MemoryUser();
    MemoryGame memoryGame = new MemoryGame();
    final UserService userService = new UserService(memoryAuth, memoryUser);
    final ClearService clearService = new ClearService(memoryAuth, memoryGame, memoryUser);
    final GameService gameService = new GameService(memoryAuth, memoryGame);

    @Test
    @DisplayName("Create Game Success")
    public void createGamePass() throws ResponseException {
        clearService.clearAll();
        System.out.print("TESTING: Create Game Success\n\n");
        String username = "Bob";
        String password = "boB";
        String email = "bob@hotmail.com";
        RegisterResponse registered =  userService.register(new RegisterRequest(username, password, email));

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
        Assertions.assertEquals(401, exception.StatusCode());
    }

    @Test
    @DisplayName("List Game Success")
    public void listGamePass() throws ResponseException {
        clearService.clearAll();
        System.out.print("TESTING: List Game Success\n\n");
        String username = "Bob";
        String password = "boB";
        String email = "bob@hotmail.com";
        RegisterResponse registered =  userService.register(new RegisterRequest(username, password, email));

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
        Assertions.assertEquals(401, exception.StatusCode());
    }

    @Test
    @DisplayName("Join Game Success")
    public void joinGamePass() throws ResponseException {
        clearService.clearAll();
        System.out.print("TESTING: List Game Success\n\n");
        String username = "Bob";
        String password = "boB";
        String email = "bob@hotmail.com";

        System.out.print("\nRegistering User:\n");
        RegisterResponse registered =  userService.register(new RegisterRequest(username, password, email));
        System.out.print(registered + "\n");

        System.out.print("\nCreating Game:\n");
        CreateGameResponse gameResponse = gameService.createGame(new CreateGameRequest(registered.authToken(), "CoolGame"));
        System.out.print(registered + "\n");

        System.out.print("\nJoining Game:\n");
        gameService.joinGame(new JoinGameRequest("WHITE", gameResponse.gameID(), registered.authToken()));
        ListGamesResponse listGames =  gameService.listGames(new ListGamesRequest(registered.authToken()));
        System.out.print(listGames + "\n");

        String username2 = "Billy";
        String password2 = "ylliB";
        String email2 = "billy@hotmail.com";

        System.out.print("\nRegistering User 2:\n");
        RegisterResponse registered2 =  userService.register(new RegisterRequest(username2, password2, email2));
        System.out.print(registered + "\n");

        System.out.print("\nUser 2 Attempts To Join Game:\n");
        gameService.joinGame(new JoinGameRequest("BLACK", gameResponse.gameID(), registered2.authToken()));


        ListGamesResponse listGamesFinal =  gameService.listGames(new ListGamesRequest(registered2.authToken()));
        System.out.print(listGamesFinal + "\n");
        Assertions.assertNotNull(listGamesFinal);
    }

    @Test
    @DisplayName("Join Game Fail: already taken")
    public void joinGameFail403() throws ResponseException {
        clearService.clearAll();
        System.out.print("TESTING: List Game Success\n\n");
        String username = "Bob";
        String password = "boB";
        String email = "bob@hotmail.com";

        System.out.print("\nRegistering User:\n");
        RegisterResponse registered =  userService.register(new RegisterRequest(username, password, email));
        System.out.print(registered + "\n");

        System.out.print("\nCreating Game:\n");
        CreateGameResponse gameResponse = gameService.createGame(new CreateGameRequest(registered.authToken(), "CoolGame"));
        System.out.print(registered + "\n");

        System.out.print("\nJoining Game:\n");
        gameService.joinGame(new JoinGameRequest("WHITE", gameResponse.gameID(), registered.authToken()));
        ListGamesResponse listGames =  gameService.listGames(new ListGamesRequest(registered.authToken()));
        System.out.print(listGames + "\n");

        String username2 = "Billy";
        String password2 = "ylliB";
        String email2 = "billy@hotmail.com";

        System.out.print("\nRegistering User 2:\n");
        RegisterResponse registered2 =  userService.register(new RegisterRequest(username2, password2, email2));
        System.out.print(registered + "\n");

        System.out.print("\nUser 2 Attempts To Join Game:\n");
        ResponseException exception  = Assertions.assertThrows(ResponseException.class, () ->
            gameService.joinGame(new JoinGameRequest("WHITE", gameResponse.gameID(), registered2.authToken()))
        );
        Assertions.assertEquals(403, exception.StatusCode());
    }
}
