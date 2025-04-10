package client;

import java.util.Arrays;
import java.util.HashMap;

import chess.ChessGame;
import chess.ChessPosition;
import client.websocket.GameHandler;
import client.websocket.WebSocketFacade;
import com.google.gson.Gson;
import exception.ResponseException;
import resreq.*;
import ui.DisplayChessBoard;
import websocket.messages.ServerMessage;

public class Client implements GameHandler {
    private HashMap<Integer, Integer> gameList = new HashMap<>();
    private String visitorName = null;
    private String authToken = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private final WebSocketFacade websocket;
    private ChessGame.TeamColor team = ChessGame.TeamColor.WHITE;
    private Integer inGameID = null;

    public Client (String serverUrl) throws ResponseException{
        server = new ServerFacade(serverUrl);
        websocket = new WebSocketFacade();
        this.serverUrl = serverUrl;
    }

    @Override
    public void updateGame(ChessGame game, ChessPosition posValids){
//        if (posValids == null){
//            DisplayChessBoard.printGame();
//        }
    }

    @Override
    public void printMessage(String message, ServerMessage.ServerMessageType type){

    }

    public String eval(String input) throws ResponseException {
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
            throw new ResponseException(400, ex.getMessage());
        }
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "Unauthorized: You must sign in");
        }
    }

    public String clientLogin(String... params) throws ResponseException {
        if (params.length == 2) {
            LoginResponse loginResponse = server.login(new LoginRequest(params[0], params[1]));
            state = State.SIGNEDIN;
            authToken = loginResponse.authToken();
            visitorName = params[0];
            clientListGames();
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Invalid Login Input. Try: login <username> <password>");
    }

    public String clientLogout() throws ResponseException {
        assertSignedIn();
        server.logout(authToken);
        state = State.SIGNEDOUT;
        authToken = null;
        String consoleResponse = String.format("%s logged off.", visitorName);
        visitorName = null;
        gameList = null;
        return consoleResponse;
    }

    public String clientRegister(String... params) throws ResponseException {
        if (params.length == 3) {
            RegisterResponse registerResponse = server.register(new RegisterRequest(params[0], params[1], params[2]));
            state = State.SIGNEDIN;
            authToken = registerResponse.authToken();
            visitorName = params[0];
            clientListGames();
            return String.format("You registered as %s.", visitorName);
        }
        throw new ResponseException(400, "Invalid Register Input. Try: register <username> <password> <email>");
    }

    public String clientCreateGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            String gameName = params[0];
            CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
            CreateGameResponse createGameResponse = server.createGame(createGameRequest);
            gameList.put(gameList.size() + 1, createGameResponse.gameID());
            return String.format("Created game %s", gameName);
        }
        throw new ResponseException(400, "Invalid Create Game Input. Try: newgame <gamename>");
    }

    public String clientListGames() throws ResponseException {
        assertSignedIn();
        ListGamesResponse listGamesResponse = server.listGames(authToken);
        var games = listGamesResponse.games();
        var result = new StringBuilder();
        var gson = new Gson();
        gameList = new HashMap<>();
        Integer clientGameNum = 1;
        for (var game : games) {
            String whiteUsername = (game.whiteUsername() == null) ? " " : game.whiteUsername();
            String blackUsername = (game.blackUsername() == null) ? " " : game.blackUsername();
            String clientGameData = String.format("%s)   Name: %s    White: %s   Black: %s\n",
                    clientGameNum, game.gameName(), whiteUsername, blackUsername);
            result.append(clientGameData);
            gameList.put(clientGameNum, game.gameID());
            clientGameNum++;
        }
//        System.out.println("\nGAMELIST: \n" + gameList);
        return result.toString();
    }

    public String clientJoinGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 2) {
            ChessGame.TeamColor teamColor;
            try{
                Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                throw new ResponseException(400,
                        String.format("%s is not a valid gameID, gameID must be numeric. Try: play <white/black> <gameID>", params[1]));
            }
            if (params[0].equalsIgnoreCase("WHITE")){
                teamColor = ChessGame.TeamColor.WHITE;
            } else if (params[0].equalsIgnoreCase("BLACK")) {
                teamColor = ChessGame.TeamColor.BLACK;
            } else {
                throw new ResponseException(400,
                        String.format("%s is not a valid team color. Try: play <white/black> <gameID>", params[0]));
            }
            try {
                Integer actualGameID = gameList.get(Integer.parseInt(params[1]));
                server.joinGame(new JoinGameRequest(teamColor, actualGameID, authToken));
            } catch (ResponseException error){
                throw new ResponseException(402, "Unauthorized: " + error.getMessage());
            }
            // websocket 
            return String.format("%s joined the game!\n", visitorName);
        }
        throw new ResponseException(400, "Invalid Join Game Input. Try: play <white/black> <gameID>");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            try {
                if (!gameList.containsKey(Integer.parseInt(params[0]))){
                    throw new ResponseException(401,
                            "No such game.");
                }
            } catch (NumberFormatException e) {
                throw new ResponseException(401,
                        "Invalid Observe Game Input, gameID must be numeric. Try: observe <gameID>");
            }
            return String.format("%s is observing the game", visitorName);
        }
        throw new ResponseException(400, "Invalid Observe Game Input. Try: observe <gameID>");
    }

        public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - help
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    """;
        }
        return """
                - help
                - newgame <gamename>
                - listgames
                - play <white/black> <game ID>
                - observe <game ID>
                - logout
                - quit
                """;
    }
}