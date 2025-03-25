package client;

import chess.ChessGame;
import exception.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import resreq.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private String workingAuth;


    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(8080);
        String workingAuth = null;
        serverFacade = new ServerFacade("http://localhost:" + port);
        serverFacade.clearAll();
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() throws ResponseException {
        serverFacade.clearAll();
        server.stop();
    }


    @Test
    public void testRegisterPositive() {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        assertDoesNotThrow(() -> {
            RegisterResponse response = serverFacade.register(request);
            assertNotNull(response);
            assertEquals("username", response.username());
            workingAuth = response.authToken();
        });
    }

    @Test
    public void testRegisterNegative() {
        RegisterRequest invalidRequest = new RegisterRequest("", "", null);
        assertThrows(ResponseException.class, () -> serverFacade.register(invalidRequest));
    }

    @Test
    public void testLoginPositive() {
        LoginRequest request = new LoginRequest("username", "password");
        assertDoesNotThrow(() -> {
            LoginResponse response = serverFacade.login(request);
            assertNotNull(response);
            workingAuth = response.authToken();
        });
    }

    @Test
    public void testLoginNegative() {
        LoginRequest invalidRequest = new LoginRequest("invalidUser", "invalidPassword");
        assertThrows(ResponseException.class, () -> serverFacade.login(invalidRequest));
    }

    @Test
    public void testLogoutPositive() {
        assertDoesNotThrow(() -> serverFacade.logout(workingAuth));
    }

    @Test
    public void testLogoutNegative() {
        String invalidAuthToken = "invalidAuthToken";
        assertThrows(ResponseException.class, () -> serverFacade.logout(invalidAuthToken));
    }

    @Test
    public void testListGamesPositive() {
        LoginRequest request = new LoginRequest("username", "password");
        assertDoesNotThrow(() -> {
            LoginResponse response = serverFacade.login(request);
            assertNotNull(response);
            workingAuth = response.authToken();
        });
        assertDoesNotThrow(() -> {
            ListGamesResponse response = serverFacade.listGames(workingAuth);
            assertNotNull(response);
            assertFalse(response.games().isEmpty());
        });
    }

    @Test
    public void testListGamesNegative() {
        String invalidAuthToken = "invalidAuthToken";
        assertThrows(ResponseException.class, () -> serverFacade.listGames(invalidAuthToken));
    }

    @Test
    public void testCreateGamePositive() {
        CreateGameRequest request = new CreateGameRequest(workingAuth, "gameName");
        assertDoesNotThrow(() -> {
            CreateGameResponse response = serverFacade.createGame(request);
            assertNotNull(response);
        });
    }

    @Test
    public void testCreateGameNegative() {
        CreateGameRequest invalidRequest = new CreateGameRequest("invalidAuthToken", "gameName");
        assertThrows(ResponseException.class, () -> serverFacade.createGame(invalidRequest));
    }

    @Test
    public void testJoinGamePositive() {
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.BLACK, 1, workingAuth);
        assertDoesNotThrow(() -> serverFacade.joinGame(request));
    }

    @Test
    public void testJoinGameNegative() {
        JoinGameRequest invalidRequest = new JoinGameRequest(ChessGame.TeamColor.BLACK, 197992, workingAuth);
        assertThrows(ResponseException.class, () -> serverFacade.joinGame(invalidRequest));
    }
}
