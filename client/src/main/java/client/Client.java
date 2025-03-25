package client;

import java.lang.module.ResolutionException;
import java.util.Arrays;
import java.util.Vector;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.*;
import model.GameData;
import resreq.*;

public class Client {
    private String visitorName = null;
    private String authToken = null;
    private Integer currGameID = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) throws ResponseException{
        var tokens = input.split(" ");
        var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "login" -> clientLogin(params);
            case "logout" -> clientLogout();
            case "register" -> clientRegister(params);
            case "newgame" -> clientCreateGame(params);
            case "listgames" -> clientListGames();
            case "play" -> clientJoinGame(params);
            case "observe" -> observeGame(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    public String clientLogin(String... params) throws ResponseException {
        if (params.length >= 2) {
            LoginResponse loginResponse = server.login(new LoginRequest(params[0], params[1]));
            state = State.SIGNEDIN;
            authToken = loginResponse.authToken();
            visitorName = params[0];
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Invalid Login Input. Expected: login <username> <password>");
    }

    public String clientLogout() throws ResponseException {
        assertSignedIn();
        server.logout(authToken);
        state = State.SIGNEDOUT;
        authToken = null;
        String consoleResponse = String.format("%s left the shop", visitorName);
        visitorName = null;
        return consoleResponse;
    }

    public String clientRegister(String... params) throws ResponseException {
        if (params.length >= 3) {
            RegisterResponse registerResponse = server.register(new RegisterRequest(params[0], params[1], params[2]));
            state = State.SIGNEDIN;
            authToken = registerResponse.authToken();
            visitorName = params[0];
            return String.format("You registered as %s.", visitorName);
        }
        throw new ResponseException(400, "Invalid Register Input. Expected: register <username> <password> <email>");
    }

    public String clientCreateGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            String gameName = params[0];
            CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
            CreateGameResponse createGameResponse = server.createGame(createGameRequest);
            return String.format("Created game %s with ID: %s", gameName, createGameResponse.gameID());
        }
        throw new ResponseException(400, "Invalid Create Game Input. Expected: newgame <gamename>");
    }

    public String clientListGames() throws ResponseException {
        assertSignedIn();
        var games = server.listGames(authToken).games();
        var result = new StringBuilder();
        var gson = new Gson();
        for (var game : games) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }

    public String clientJoinGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 2) {
            ChessGame.TeamColor teamColor = (params[0].equalsIgnoreCase("WHITE")) ?
                    ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            try {
                currGameID = Integer.valueOf(params[1]);
            } catch (Throwable error) {
                String msg = error.toString();
                throw new ResponseException(401, "Invalid Join Game Input. Expected: play <white/black> <gameID>\n" + msg);
            }
            server.joinGame(new JoinGameRequest(teamColor, Integer.parseInt(params[1]), authToken));


            return String.format("%s joined the game!", visitorName);
        }
        throw new ResponseException(400, "Invalid Join Game Input. Expected: play <white/black> <gameID>");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            try {
                currGameID = Integer.valueOf(params[1]);
            } catch (Throwable error) {
                String msg = error.toString();
                throw new ResponseException(401, "Invalid Observe Game Input. Expected: play <gameID>\n" + msg);
            }
            return String.format("%s is observing gameID %s", visitorName, params[0]);
        }
        throw new ResponseException(400, "Invalid Observe Game Input. Expected: observe <gameID>");
    }

        public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    """;
        }
        return """
                - newgame <gamename>
                - listgames
                - play <white/black> <game ID>
                - observe <game ID>
                - logout
                - quit
                """;
    }
}