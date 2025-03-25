package client;

import chess.ChessGame;
import exception.ResponseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resreq.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        serverFacade = new ServerFacade("http://localhost:" + port);
        serverFacade.clearAll();
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void clearer() throws ResponseException {
        serverFacade.clearAll();
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
            assertTrue(response.authToken().length() >= 10);
        });
    }

    @Test
    public void testRegisterNegative() {
        RegisterRequest invalidRequest = new RegisterRequest("", "", null);
        assertThrows(ResponseException.class, () -> serverFacade.register(invalidRequest));
    }

    @Test
    public void testLoginPositive() {
        RegisterRequest registerRequest = new RegisterRequest("username1", "password1", "email1");
        LoginRequest loginRequest = new LoginRequest("username1", "password1");
        assertDoesNotThrow(() -> {
            RegisterResponse registerResponse = serverFacade.register(registerRequest);
            LoginResponse loginResponse = serverFacade.login(loginRequest);
            assertNotNull(loginResponse);
        });
    }

    @Test
    public void testLoginNegative() {
        LoginRequest invalidRequest = new LoginRequest("invalidUser", "invalidPassword");
        assertThrows(ResponseException.class, () -> serverFacade.login(invalidRequest));
    }

    @Test
    public void testLogoutPositive() {
        RegisterRequest registerRequest = new RegisterRequest("user1", "pass1", "em1");
        assertDoesNotThrow(() -> {
            RegisterResponse registerResponse = serverFacade.register(registerRequest);
            serverFacade.logout(registerResponse.authToken());
        });
    }

    @Test
    public void testLogoutNegative() {
        assertThrows(ResponseException.class, () -> serverFacade.logout("invalidAuthToken"));
    }

    @Test
    public void testCreateGamePositive() {
        RegisterRequest registerRequest = new RegisterRequest("us3rn", "w0rd", "em4");

        assertDoesNotThrow(() -> {
            RegisterResponse resRes = serverFacade.register(registerRequest);
            CreateGameRequest request = new CreateGameRequest(resRes.authToken(), "gameName");
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
    public void testListGamesPositive() {
        RegisterRequest registerRequest = new RegisterRequest("us3rname", "passw0rd", "em4il");
        assertDoesNotThrow(() -> {
            RegisterResponse regRes = serverFacade.register(registerRequest);
            serverFacade.createGame(new CreateGameRequest(regRes.authToken(), "game"));
            ListGamesResponse listGamesResponse = serverFacade.listGames(regRes.authToken());
            assertNotNull(listGamesResponse);
            assertFalse(listGamesResponse.games().isEmpty());
        });
    }

    @Test
    public void testListGamesNegative() {
        String invalidAuthToken = "invalidAuthToken";
        assertThrows(ResponseException.class, () -> serverFacade.listGames(invalidAuthToken));
    }

    @Test
    public void testJoinGamePositive() {
        RegisterRequest registerRequest = new RegisterRequest("us3rname", "passw0rd", "em4il");
        assertDoesNotThrow(() -> {
            RegisterResponse regRes = serverFacade.register(registerRequest);
            serverFacade.createGame(new CreateGameRequest(regRes.authToken(), "game1"));
            JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.BLACK, 1, regRes.authToken());
            serverFacade.joinGame(joinGameRequest);
        });
    }

    @Test
    public void testJoinGameNegative() {
        JoinGameRequest invalidRequest = new JoinGameRequest(ChessGame.TeamColor.BLACK, 197992, "badAuth");
        assertThrows(ResponseException.class, () -> serverFacade.joinGame(invalidRequest));
    }

    @Test
    public void testClearPositive() {
        RegisterRequest reg = new RegisterRequest("name", "pass", "Gmail");
        assertDoesNotThrow(() -> {
            RegisterResponse regRes = serverFacade.register(reg);
            serverFacade.createGame(new CreateGameRequest(regRes.authToken(), "game1"));
            JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.BLACK, 1, regRes.authToken());
            serverFacade.joinGame(joinGameRequest);
            serverFacade.clearAll();
            serverFacade.register(reg);
        });

    }
}
