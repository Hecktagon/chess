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
    @DisplayName("Create Game Success")
    public void createGameFail401() throws ResponseException {
        clearService.clearAll();
        ResponseException exception  = Assertions.assertThrows(ResponseException.class, () ->
        gameService.createGame(new CreateGameRequest(null, "CoolGame"))
        );
        Assertions.assertEquals(401, exception.StatusCode());
    }

    @Test
    @DisplayName("List Game Success")
    public void ListGamePass() throws ResponseException {
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


}
