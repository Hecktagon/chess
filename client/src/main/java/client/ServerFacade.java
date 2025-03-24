package client;

import com.google.gson.Gson;
//import exception.ErrorResponse;
import exception.*;
import resreq.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clearAll() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public RegisterResponse register(RegisterRequest registerRequest) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, registerRequest, null, RegisterResponse.class);
    }

    public LoginResponse login(LoginRequest loginRequest) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, loginRequest, null, LoginResponse.class);
    }

    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, authToken, null);
    }

    public ListGamesResponse listGames(String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, null, authToken,  ListGamesResponse.class);
    }

    public CreateGameResponse createGame(CreateGameRequest createGameRequest, String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, createGameRequest, authToken, CreateGameResponse.class);
    }

    public void joinGame(JoinGameRequest joinGameRequest, String authToken) throws ResponseException {
        var path = "/game";
        this.makeRequest("PUT", path, joinGameRequest, authToken, JoinGameRequest.class);
    }

    private <T> T makeRequest(String method, String path, Object request, String header, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            // if an authToken header is given, adds it to header under Authorization
            if (header != null){
                http.setRequestProperty("Authorization", String.format("Bearer %s", header));
            }
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}


// petshop code:

//package server;
//
//import com.google.gson.Gson;
//import exception.ErrorResponse;
//import exception.ResponseException;
//import model.Pet;
//
//import java.io.*;
//        import java.net.*;
//
//public class ServerFacade {
//
//    private final String serverUrl;
//
//    public ServerFacade(String url) {
//        serverUrl = url;
//    }
//
//
//    public Pet addPet(Pet pet) throws ResponseException {
//        var path = "/pet";
//        return this.makeRequest("POST", path, pet, Pet.class);
//    }
//
//    public void deletePet(int id) throws ResponseException {
//        var path = String.format("/pet/%s", id);
//        this.makeRequest("DELETE", path, null, null);
//    }
//
//    public void deleteAllPets() throws ResponseException {
//        var path = "/pet";
//        this.makeRequest("DELETE", path, null, null);
//    }
//
//    public Pet[] listPets() throws ResponseException {
//        var path = "/pet";
//        record listPetResponse(Pet[] pet) {
//        }
//        var response = this.makeRequest("GET", path, null, listPetResponse.class);
//        return response.pet();
//    }
//
//    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
//        try {
//            URL url = (new URI(serverUrl + path)).toURL();
//            HttpURLConnection http = (HttpURLConnection) url.openConnection();
//            http.setRequestMethod(method);
//            http.setDoOutput(true);
//
//            writeBody(request, http);
//            http.connect();
//            throwIfNotSuccessful(http);
//            return readBody(http, responseClass);
//        } catch (ResponseException ex) {
//            throw ex;
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//
//
//    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
//        if (request != null) {
//            http.addRequestProperty("Content-Type", "application/json");
//            String reqData = new Gson().toJson(request);
//            try (OutputStream reqBody = http.getOutputStream()) {
//                reqBody.write(reqData.getBytes());
//            }
//        }
//    }
//
//    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
//        var status = http.getResponseCode();
//        if (!isSuccessful(status)) {
//            try (InputStream respErr = http.getErrorStream()) {
//                if (respErr != null) {
//                    throw ResponseException.fromJson(respErr);
//                }
//            }
//
//            throw new ResponseException(status, "other failure: " + status);
//        }
//    }
//
//    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
//        T response = null;
//        if (http.getContentLength() < 0) {
//            try (InputStream respBody = http.getInputStream()) {
//                InputStreamReader reader = new InputStreamReader(respBody);
//                if (responseClass != null) {
//                    response = new Gson().fromJson(reader, responseClass);
//                }
//            }
//        }
//        return response;
//    }
//
//
//    private boolean isSuccessful(int status) {
//        return status / 100 == 2;
//    }
//}
