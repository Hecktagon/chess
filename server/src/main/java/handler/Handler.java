package handler;
import spark.*;

public class Handler {

    private Object handleClearAll(Request req, Response res) throws ResponseException {
        service.deleteAllPets();
        res.status(204);
        return "";
    }
}



//PetShop handlers:


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
