package client;

import java.util.Arrays;
import java.util.HashMap;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.GameHandler;
import client.websocket.WebSocketFacade;
import com.google.gson.Gson;
import exception.ResponseException;
import resreq.*;
import ui.DisplayChessBoard;
import ui.EscapeSequences;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public class Client implements GameHandler {
    private HashMap<Integer, Integer> gameList = new HashMap<>();
    private String visitorName = null;
    private String authToken = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private WebSocketFacade websocket;
    private ChessGame.TeamColor team = ChessGame.TeamColor.WHITE;
    private Integer inGameID = null;
    private boolean observing = false;
    private ChessGame curGame = null;

    public Client (String serverUrl) throws ResponseException{
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    @Override
    public void updateGame(ChessGame game, ChessPosition posValids){
//        System.out.println(SET_TEXT_COLOR_GREEN + "UPDATING GAME" + RESET_TEXT_COLOR);
        curGame = game;
        DisplayChessBoard.printChessGame(game, team, posValids);
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> ");
    }

    @Override
    public void printMessage(String message, ServerMessage.ServerMessageType type){
        if (type == ServerMessage.ServerMessageType.ERROR){
            System.out.print("\n" + SET_TEXT_COLOR_RED + message + "\n");
        } else {
            System.out.print("\n" + RESET_TEXT_COLOR + message);
        }
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> ");
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

    private static int letterToCol(String letter) {
        letter = letter.toLowerCase();

        HashMap<String, Integer> letterToNumberMap = new HashMap<>();
        letterToNumberMap.put("a", 1);
        letterToNumberMap.put("b", 2);
        letterToNumberMap.put("c", 3);
        letterToNumberMap.put("d", 4);
        letterToNumberMap.put("e", 5);
        letterToNumberMap.put("f", 6);
        letterToNumberMap.put("g", 7);
        letterToNumberMap.put("h", 8);

        return letterToNumberMap.getOrDefault(letter, -1);
    }

    private ChessPiece.PieceType shortcutToPiece(String shortcut){
        HashMap<String, ChessPiece.PieceType> shortcuts = new HashMap<>();

        shortcuts.put("Q", ChessPiece.PieceType.QUEEN);
        shortcuts.put("K", ChessPiece.PieceType.KING);
        shortcuts.put("R", ChessPiece.PieceType.ROOK);
        shortcuts.put("N", ChessPiece.PieceType.KNIGHT);
        shortcuts.put("B", ChessPiece.PieceType.BISHOP);
        shortcuts.put("P", ChessPiece.PieceType.PAWN);

        return shortcuts.get(shortcut);
    }

    private boolean inBounds(Integer x, Integer y){
        return 1 <= x && x <= 8 && 1 <= y && y <= 8;
    }

    private ChessPosition chessPositionProcessor(String stringPos)throws ResponseException{
        String strCol = String.valueOf(stringPos.charAt(0));
        Integer row = Integer.parseInt(String.valueOf(stringPos.charAt(1)));
        Integer col = letterToCol(strCol);
        return new ChessPosition(row, col);
    }

    private ChessMove chessNotationProcessor(String stringMove) throws ResponseException{
        ChessMove move;
        try {
            String startStrCol = String.valueOf(stringMove.charAt(0));
            Integer startRow = Integer.parseInt(String.valueOf(stringMove.charAt(1)));
            Integer startCol = letterToCol(startStrCol);
            String endStrCol = String.valueOf(stringMove.charAt(2));
            Integer endRow =  Integer.parseInt(String.valueOf(stringMove.charAt(3)));
            Integer endCol = letterToCol(endStrCol);

            ChessPiece.PieceType promoPiece = null;
            if (stringMove.length() == 5){
                promoPiece = shortcutToPiece(String.valueOf(stringMove.charAt(5)));
            }
            if (!inBounds(startRow, startCol) || !inBounds(endRow, endCol)){
                throw new ResponseException(400, "Invalid makemove Input.");
            }

            move = new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol), promoPiece);
        } catch(Exception e) {
            throw new ResponseException(400, "Invalid makemove Input. Try: makemove <chessmove>," +
                    "\n(ex. <e2e4> or <f3h3> or <b7b8Q> for a promotion to Queen");
        }
        return move;
    }

    private void gameCheck(String errMessage)throws ResponseException {
        if (curGame == null) {
            throw new ResponseException(400, errMessage);
        }
        if (authToken == null) {
            throw new ResponseException(400, errMessage);
        }
        if (!gameList.containsKey(inGameID)) {
            throw new ResponseException(400, errMessage);
        }
    }

    private void leaveGameReset(){
        inGameID = null;
        observing = false;
        team = ChessGame.TeamColor.WHITE;
    }

    public String clientRedraw() throws ResponseException{
        assertSignedIn();
        gameCheck("You can't draw a game right now");
        updateGame(curGame, null);

        return "board redrawn!";
    }

    public String clientLeave()throws ResponseException{
        assertSignedIn();
        gameCheck("You can't leave a game right now");
        websocket.leaveGame(authToken, gameList.get(inGameID));
        websocket = null;
        leaveGameReset();

        return "You left the game";
    }

    public String clientMakeMove(String... params) throws ResponseException{
        assertSignedIn();
        if (params.length != 1){
            throw new ResponseException(400, "Invalid makemove Input. Try: makemove <chessmove>," +
                    "\n(ex. <e2e4> or <f3h3> or <b7b8Q> for a promotion to Queen");
        }
        gameCheck("You can't make a move right now");
        String stringMove = params[0];
        if (!(stringMove.length() == 4 || stringMove.length() == 5)) {
            throw new ResponseException(401, "Invalid makemove Input. Try: makemove <chessmove>," +
                    "\n(ex. <e2e4> or <f3h3>");
        }

        ChessMove move = chessNotationProcessor(stringMove);

        websocket.makeMove(authToken, gameList.get(inGameID), move);
        return "You made move: " + stringMove;

    }

    public String clientResign() throws ResponseException{
        assertSignedIn();
        gameCheck("You can't resign right now");
        websocket.resignGame(authToken,gameList.get(inGameID));
        return "You resigned";
    }

    public String clientShowLegalMoves(String... params) throws ResponseException{
        assertSignedIn();
        gameCheck("You can't check moves right now");
        if (params.length != 1 || params[0].length() != 2){
            throw new ResponseException(400, "Invalid showmoves Input. Try: showmoves <position> (ex. b4)");
        }

        updateGame(curGame, chessPositionProcessor(params[0]));
        return "Showing moves for " + params[0];
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
//            System.out.println(SET_TEXT_COLOR_GREEN + "JOINED GAME" + RESET_TEXT_COLOR);
            websocket = new WebSocketFacade(this);
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
            websocket = new WebSocketFacade(this);
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