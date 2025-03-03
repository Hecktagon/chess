package service;

import dataAccess.MemoryAuth;
import dataAccess.MemoryGame;
import dataAccess.MemoryUser;
import exception.ResponseException;
import resReq.RegisterRequest;
import resReq.RegisterResponse;
import service.UserService;
import service.ClearService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserServiceTests {

    static final UserService userService = new UserService(new MemoryAuth(), new MemoryUser());
    static final ClearService clearService = new ClearService(new MemoryAuth(), new MemoryGame(), new MemoryUser());

    @BeforeEach
    void clear() throws ResponseException {
        clearService.clearAll();
    }

    @Test
    @DisplayName("Register Response Success")
    public void regResPass() throws ResponseException {
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
    public void regResFail400() throws ResponseException {
        ResponseException exception  = Assertions.assertThrows(ResponseException.class, () ->
        userService.register(new RegisterRequest(null, null, null))
    );
        Assertions.assertEquals(400, exception.StatusCode());
    }

    @Test
    @DisplayName("Register Response Fail: already taken")
    public void regResFail403() throws ResponseException {
        String username = "Bob";
        String password = "boB";
        String email = "bob@hotmail.com";

        userService.register(new RegisterRequest(username, password, email));

        ResponseException exception  = Assertions.assertThrows(ResponseException.class, () ->
                userService.register(new RegisterRequest(username, password, email))
        );
        Assertions.assertEquals(403, exception.StatusCode());
    }
}
