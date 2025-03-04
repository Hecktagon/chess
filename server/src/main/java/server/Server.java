package server;
import dataAccess.*;
import model.UserData;
import service.GameService;
import service.UserService;
import spark.*;
import com.google.gson.Gson;
import exception.ResponseException;
import model.UserData;
import service.ClearService;
import resReq.*;

import java.util.Map;


public class Server {
    ClearService clearService;
    UserService userService;
    GameService gameService;

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        AuthDAO authDAO = new MemoryAuth();
        UserDAO userDAO = new MemoryUser();
        GameDAO gameDAO = new MemoryGame();

        clearService = new ClearService(authDAO, gameDAO, userDAO);
        userService = new UserService(authDAO, userDAO);
        gameService = new GameService(authDAO, gameDAO);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::handleClearAll);
        Spark.post("/user", this::handleRegister);
        Spark.post("/session", this::handleLogin);
        Spark.delete("/session", this::handleLogout);
        Spark.get("/game", this::handleListGames);
        Spark.post("/game", this::handleCreateGame);
        Spark.put("/game", this::handleJoinGame);
        Spark.exception(ResponseException.class, this::handleException);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    // HANDLERS:

    private Object handleClearAll(Request req, Response res) throws ResponseException {
        clearService.clearAll();
        res.status(200);
        return "";
    }


    // the auth token is in the headers under "Authorization"
    private Object handleRegister(Request req, Response res) throws ResponseException {
        RegisterRequest user = new Gson().fromJson(req.body(), RegisterRequest.class);

        RegisterResponse registerResponse = userService.register(user);
        res.status(200);
        return new Gson().toJson(registerResponse);
    }

    private Object handleLogin(Request req, Response res) throws ResponseException {
        LoginRequest loginReq = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResponse loginRes = userService.login(loginReq);
        res.status(200);
        return new Gson().toJson(loginRes);
    }

    private Object handleLogout(Request req, Response res) throws ResponseException {
        String authToken = req.headers("Authorization");
        EmptyResponse logoutRes = userService.logout(new LogoutRequest(authToken));
        res.status(200);
        return "";
    }

    private Object handleListGames(Request req, Response res) throws ResponseException {
        String authToken = req.headers("Authorization");
        ListGamesResponse gameList = gameService.listGames(new ListGamesRequest(authToken));
        return new Gson().toJson(Map.of("games", gameList.games()));
    }

    private Object handleCreateGame(Request req, Response res) throws ResponseException {
        String authToken = req.headers("Authorization");
        CreateGameRequest partialGameResponse = new Gson().fromJson(req.body(), CreateGameRequest.class);
        CreateGameResponse gameResponse = gameService.createGame(new CreateGameRequest(authToken, partialGameResponse.gameName()));
        res.status(200);
        return new Gson().toJson(gameResponse);
    }

    private Object handleJoinGame(Request req, Response res) throws ResponseException {
        String authToken = req.headers("Authorization");
        JoinGameRequest partial = new Gson().fromJson(req.body(), JoinGameRequest.class);
        EmptyResponse joinResponse = gameService.joinGame(new JoinGameRequest(partial.playerColor(), partial.gameID(), authToken));
        res.status(200);
        return "";
    }

    private void handleException(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }
}
