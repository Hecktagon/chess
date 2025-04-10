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

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

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
    private boolean observing = false;

    public Client (String serverUrl) throws ResponseException{
        server = new ServerFacade(serverUrl);
        websocket = new WebSocketFacade();
        this.serverUrl = serverUrl;
    }

    @Override
    public void updateGame(ChessGame game, ChessPosition posValids){
        DisplayChessBoard.printChessGame(game, team, posValids);
    }

    @Override
    public void printMessage(String message, ServerMessage.ServerMessageType type){
        if (type == ServerMessage.ServerMessageType.ERROR){
            System.out.print("\n" + SET_TEXT_COLOR_RED + message + "\n");
        } else {
            System.out.print("\n" + RESET_TEXT_COLOR + message);
        }
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
                case "redraw" -> clientRedraw();
                case "leave" -> clientLeave();
                case "makemove" -> clientMakeMove(params);
                case "resign" -> clientResign();
                case "showmoves" -> clientShowLegalMoves(params);
                case "quit" -> clientQuit();
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

    private void leaveGameReset(){
        inGameID = null;
        observing = false;
        team = ChessGame.TeamColor.WHITE;
    }

    // TODO: what is quit supposed to do if you're in a game
    public String clientQuit() throws ResponseException{
        return "quit";
    }

    public String clientRedraw() throws ResponseException{
        assertSignedIn();
        if (authToken == null){
            throw new ResponseException(400, "You can't draw a game right now");
        }
        if (!gameList.containsKey(inGameID)) {
            throw new ResponseException(400, "You can't draw a game right now");
        }
    }

    public String clientLeave()throws ResponseException{
        assertSignedIn();
        if (authToken == null){
            throw new ResponseException(400, "You can't leave a game right now");
        }
        if (!gameList.containsKey(inGameID)){
            throw new ResponseException(400, "You can't leave a game right now");
        } else {
            websocket.leaveGame(authToken, gameList.get(inGameID));
            leaveGameReset();
        }
        return "You left the game";
    }

    public String clientMakeMove(String... params) throws ResponseException{
        assertSignedIn();
        return null;
    }

    public String clientResign() throws ResponseException{
        assertSignedIn();
        return null;
    }

    public String clientShowLegalMoves(String... params) throws ResponseException{
        assertSignedIn();
        return null;
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
        ChessGame.TeamColor teamColor;
        if (params.length == 2) {
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
            team = teamColor;
            inGameID = Integer.parseInt(params[1]);
            websocket.connect(authToken, gameList.get(inGameID));
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
            inGameID = Integer.parseInt(params[0]);
            websocket.connect(authToken, gameList.get(inGameID));
            observing = true;
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
        }else {
            if (state == State.SIGNEDIN && gameList.containsKey(inGameID) && !observing) {
                return """
                        - help
                        - redraw (redraws the chessboard)
                        - leave
                        - makemove <move in chess notation>
                        - resign
                        - showmoves <piece position>
                        - quit
                        """;
            } else if (state == State.SIGNEDIN && gameList.containsKey(inGameID) && observing) {
                return """
                    - help
                    - redraw (redraws the chessboard)
                    - leave
                    - showmoves <piece position>
                    - quit
                    """;
            } else {
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
    }
}