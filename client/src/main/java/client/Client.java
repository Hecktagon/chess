package client;

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

    public String eval(String input) {
        try {
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
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
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
        throw new ResponseException(400, "Invalid Login Input: Expected <login username password>");
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
        throw new ResponseException(400, "Invalid Register Input: Expected <register username password email>");
    }

    public String clientCreateGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            String gameName = params[0];
            CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
            CreateGameResponse createGameResponse = server.createGame(createGameRequest);
            return String.format("Created game %s with ID: %s", gameName, createGameResponse.gameID());
        }
        throw new ResponseException(400, "Invalid Create Game Input: Expected <newgame gamename>");
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
            server.joinGame(new JoinGameRequest(teamColor, Integer.parseInt(params[1]), authToken));
            currGameID = Integer.valueOf(params[1]);
            return String.format("%s joined the game!", visitorName);
        }
        throw new ResponseException(400, "Invalid Join Game Input: Expected <play white/black gameID>");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            currGameID = Integer.valueOf(params[0]);
            return String.format("%s is observing gameID %s", visitorName, params[0]);
        }
    }
}


// petshop code:

//package client;
//
//import java.util.Arrays;
//
//import com.google.gson.Gson;
//import model.Pet;
//import model.PetType;
//import exception.ResponseException;
//import client.websocket.NotificationHandler;
//import server.ServerFacade;
//import client.websocket.WebSocketFacade;
//
//public class PetClient {
//    private String visitorName = null;
//    private final ServerFacade server;
//    private final String serverUrl;
//    private final NotificationHandler notificationHandler;
//    private WebSocketFacade ws;
//    private State state = State.SIGNEDOUT;
//
//    public PetClient(String serverUrl, NotificationHandler notificationHandler) {
//        server = new ServerFacade(serverUrl);
//        this.serverUrl = serverUrl;
//        this.notificationHandler = notificationHandler;
//    }
//
//    public String eval(String input) {
//        try {
//            var tokens = input.toLowerCase().split(" ");
//            var cmd = (tokens.length > 0) ? tokens[0] : "help";
//            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
//            return switch (cmd) {
//                case "signin" -> signIn(params);
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
//                case "quit" -> "quit";
//                default -> help();
//            };
//        } catch (ResponseException ex) {
//            return ex.getMessage();
//        }
//    }
//
//    public String signIn(String... params) throws ResponseException {
//        if (params.length >= 1) {
//            state = State.SIGNEDIN;
//            visitorName = String.join("-", params);
//            return String.format("You signed in as %s.", visitorName);
//        }
//        throw new ResponseException(400, "Expected: <yourname>");
//    }
//
//    public String rescuePet(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length >= 2) {
//            var name = params[0];
//            var type = PetType.valueOf(params[1].toUpperCase());
//            var pet = new Pet(0, name, type);
//            pet = server.addPet(pet);
//            return String.format("You rescued %s. Assigned ID: %d", pet.name(), pet.id());
//        }
//        throw new ResponseException(400, "Expected: <name> <CAT|DOG|FROG>");
//    }
//
//    public String listPets() throws ResponseException {
//        assertSignedIn();
//        var pets = server.listPets();
//        var result = new StringBuilder();
//        var gson = new Gson();
//        for (var pet : pets) {
//            result.append(gson.toJson(pet)).append('\n');
//        }
//        return result.toString();
//    }
//
//    public String adoptPet(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length == 1) {
//            try {
//                var id = Integer.parseInt(params[0]);
//                var pet = getPet(id);
//                if (pet != null) {
//                    server.deletePet(id);
//                    return String.format("%s says %s", pet.name(), pet.sound());
//                }
//            } catch (NumberFormatException ignored) {
//            }
//        }
//        throw new ResponseException(400, "Expected: <pet id>");
//    }
//
//    public String adoptAllPets() throws ResponseException {
//        assertSignedIn();
//        var buffer = new StringBuilder();
//        for (var pet : server.listPets()) {
//            buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
//        }
//
//        server.deleteAllPets();
//        return buffer.toString();
//    }
//
//    public String signOut() throws ResponseException {
//        assertSignedIn();
//        ws.leavePetShop(visitorName);
//        ws = null;
//        state = State.SIGNEDOUT;
//        return String.format("%s left the shop", visitorName);
//    }
//
//    private Pet getPet(int id) throws ResponseException {
//        for (var pet : server.listPets()) {
//            if (pet.id() == id) {
//                return pet;
//            }
//        }
//        return null;
//    }
//
//    public String help() {
//        if (state == State.SIGNEDOUT) {
//            return """
//                    - signIn <yourname>
//                    - quit
//                    """;
//        }
//        return """
//                - list
//                - adopt <pet id>
//                - rescue <name> <CAT|DOG|FROG|FISH>
//                - adoptAll
//                - signOut
//                - quit
//                """;
//    }
//
//    private void assertSignedIn() throws ResponseException {
//        if (state == State.SIGNEDOUT) {
//            throw new ResponseException(400, "You must sign in");
//        }
//    }
//}
