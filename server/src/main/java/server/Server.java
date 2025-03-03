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
//        gameService = new GameService(authDAO, gameDAO);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::handleClearAll);
        Spark.post("/user", this::handleRegister);
//        Spark.post("/session", this::handleLogin);
//        Spark.delete("/session", this::handleLogout);
//        Spark.get("/game", this::handleListGames);
//        Spark.post("/game", this::handleCreateGame);
//        Spark.put("/game", this::handleJoinGame);
        Spark.exception(ResponseException.class, this::handleException);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

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

//    private Object handleLogin(Request req, Response res) throws ResponseException {
//        var pet = new Gson().fromJson(req.body(), Pet.class);
//        pet = service.addPet(pet);
//        return new Gson().toJson(pet);
//    }
//
//    private Object handleLogout(Request req, Response res) throws ResponseException {
//        var id = Integer.parseInt(req.params(":id"));
//        var pet = service.getPet(id);
//        if (pet != null) {
//            service.deletePet(id);
//            res.status(204);
//        } else {
//            res.status(404);
//        }
//        return "";
//    }
//
//    private Object handleListGames(Request req, Response res) throws ResponseException {
//        res.type("application/json");
//        var list = service.listPets().toArray();
//        return new Gson().toJson(Map.of("pet", list));
//    }
//
//    private Object handleCreateGame(Request req, Response res) throws ResponseException {
//        var pet = new Gson().fromJson(req.body(), Pet.class);
//        pet = service.addPet(pet);
//        return new Gson().toJson(pet);
//    }
//
//    private Object handleJoinGame(Request req, Response res) throws ResponseException {
//        var pet = new Gson().fromJson(req.body(), Pet.class);
//        pet = service.addPet(pet);
//        return new Gson().toJson(pet);
//    }
//
    private void handleException(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }
}




//PETSHOP EXAMPLE:


//package server;
//
//import com.google.gson.Gson;
//import exception.ResponseException;
//import model.Pet;
//import server.websocket.WebSocketHandler;
//import service.PetService;
//import spark.*;
//
//        import java.util.Map;
//
//public class PetServer {
//    private final PetService service;
//    private final WebSocketHandler webSocketHandler;
//
//    public PetServer(PetService service) {
//        this.service = service;
//        webSocketHandler = new WebSocketHandler();
//    }
//
//    public PetServer run(int port) {
//        Spark.port(port);
//
//        Spark.staticFiles.location("public");
//
//        Spark.webSocket("/ws", webSocketHandler);
//
//        Spark.post("/pet", this::addPet);
//        Spark.get("/pet", this::listPets);
//        Spark.delete("/pet/:id", this::deletePet);
//        Spark.delete("/pet", this::deleteAllPets);
//        Spark.exception(ResponseException.class, this::exceptionHandler);
//
//        Spark.awaitInitialization();
//        return this;
//    }
//
//    public int port() {
//        return Spark.port();
//    }
//
//    public void stop() {
//        Spark.stop();
//    }
//
//    private void exceptionHandler(ResponseException ex, Request req, Response res) {
//        res.status(ex.StatusCode());
//        res.body(ex.toJson());
//    }
//
//    private Object addPet(Request req, Response res) throws ResponseException {
//        var pet = new Gson().fromJson(req.body(), Pet.class);
//        pet = service.addPet(pet);
//        webSocketHandler.makeNoise(pet.name(), pet.sound());
//        return new Gson().toJson(pet);
//    }
//
//    private Object listPets(Request req, Response res) throws ResponseException {
//        res.type("application/json");
//        var list = service.listPets().toArray();
//        return new Gson().toJson(Map.of("pet", list));
//    }
//
//
//    private Object deletePet(Request req, Response res) throws ResponseException {
//        var id = Integer.parseInt(req.params(":id"));
//        var pet = service.getPet(id);
//        if (pet != null) {
//            service.deletePet(id);
//            webSocketHandler.makeNoise(pet.name(), pet.sound());
//            res.status(204);
//        } else {
//            res.status(404);
//        }
//        return "";
//    }
//
//    private Object deleteAllPets(Request req, Response res) throws ResponseException {
//        service.deleteAllPets();
//        res.status(204);
//        return "";
//    }
//}