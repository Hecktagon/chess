package service;

import dataAccess.MemoryAuth;
import dataAccess.MemoryGame;
import dataAccess.MemoryUser;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import resReq.*;
import service.UserService;
import service.ClearService;

public class UserServiceTests {
    MemoryAuth memoryAuth = new MemoryAuth();
    MemoryUser memoryUser = new MemoryUser();
    MemoryGame memoryGame = new MemoryGame();
    final UserService userService = new UserService(memoryAuth, memoryUser);
    final ClearService clearService = new ClearService(memoryAuth, memoryGame, memoryUser);

    @Test
    @DisplayName("Register Response Success")
    public void registerPass() throws ResponseException {
        clearService.clearAll();
        System.out.print("TESTING: Register Response Success\n\n");
        String username = "Bob";
        String password = "boB";
        String email = "bob@hotmail.com";
        RegisterResponse actual = userService.register(new RegisterRequest(username, password, email));
        System.out.print("Response:\n");
        System.out.print(actual + "\n");
        Assertions.assertNotNull(actual);

        System.out.print("\nPASSED :)\n\n");
    }

    @Test
    @DisplayName("Register Response Fail: bad request")
    public void registerFail400() throws ResponseException {
        clearService.clearAll();
        ResponseException exception  = Assertions.assertThrows(ResponseException.class, () ->
        userService.register(new RegisterRequest(null, null, null))
    );
        Assertions.assertEquals(400, exception.StatusCode());
    }

    @Test
    @DisplayName("Register Response Fail: already taken")
    public void registerFail403() throws ResponseException {
        clearService.clearAll();
        String username = "Bob";
        String password = "boB";
        String email = "bob@hotmail.com";

        userService.register(new RegisterRequest(username, password, email));

        ResponseException exception  = Assertions.assertThrows(ResponseException.class, () ->
                userService.register(new RegisterRequest(username, password, email))
        );
        Assertions.assertEquals(403, exception.StatusCode());
    }

    @Test
    @DisplayName("Login Success")
    public void loginPass() throws ResponseException {
        clearService.clearAll();
        System.out.print("TESTING: Login Success\n\n");
        String username = "Bob";
        String password = "boB";
        String email = "bob@hotmail.com";

        userService.register(new RegisterRequest(username, password, email));
        LoginResponse actual = userService.login(new LoginRequest(username, password));
        System.out.print("Response:\n");
        System.out.print(actual + "\n");
        Assertions.assertNotNull(actual);

        System.out.print("\nPASSED :)\n\n");
    }

    @Test
    @DisplayName("Login Fail: No Such User")
    public void loginFail401() throws ResponseException {
        clearService.clearAll();
        String username = "Bob";
        String password = "boB";

        ResponseException exception  = Assertions.assertThrows(ResponseException.class, () ->
        userService.login(new LoginRequest(username, password))
        );

        Assertions.assertEquals(401, exception.StatusCode());
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutPass() throws ResponseException {
        clearService.clearAll();
        System.out.print("TESTING: Logout Success\n\n");
        String username = "Bob";
        String password = "boB";
        String email = "bob@hotmail.com";

        userService.register(new RegisterRequest(username, password, email));
        LoginResponse loginRes = userService.login(new LoginRequest(username, password));
        EmptyResponse actual = userService.logout(new LogoutRequest(loginRes.authToken()));
        Assertions.assertNotEquals(actual, loginRes);

        System.out.print("\nPASSED :)\n\n");
    }

    @Test
    @DisplayName("Logout Fail: unauthorized")
    public void logoutFail401() throws ResponseException {
        clearService.clearAll();
        String username = "Bob";
        String password = "boB";
        String email = "bob@hotmail.com";

        userService.register(new RegisterRequest(username, password, email));
        ResponseException exception  = Assertions.assertThrows(ResponseException.class, () ->
                userService.logout(new LogoutRequest("NOTANAUTHTOKEN")));

        Assertions.assertEquals(401, exception.StatusCode());
    }



}
