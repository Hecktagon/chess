package client.websocket;

// extends endpoint and implements some interface
// Not sure what "endpoint" is, is it Server? Also, what interface does this implement?

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

public class WebSocketFacade extends Endpoint{
        Session session;
    //    GameHandler gameHandler;

        @Override //overridden from endpoint
        public void onOpen(Session session, EndpointConfig endpointConfig){

        }
    //
    //    @Override
    //    public void onClose(...); overridden from endpoint
    //
    //    @Override
    //    public void onError(...); overridden from endpoint

    // OUTGOING MESSAGES
    //    connect(...)
    //    makeMove(...)
    //    leaveGame(...)
    //    resignGame(...)

    //    private
    //    sendMessage(...)
    //        1. create command message
    //        2. send message to server

    // PROCESS INCOMING MESSAGES:
    //    onMessage(message)
    //        1. deserialize message
    //        2. call GameHandler to process message
}



// petshop WebSocketFacade:

//package client.websocket;

//import com.google.gson.Gson;
//import exception.ResponseException;
//import webSocketMessages.Action;
//import webSocketMessages.Notification;
//
//import javax.websocket.*;
//        import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
//public class WebSocketFacade extends Endpoint {
//
//    Session session;
//    NotificationHandler notificationHandler;
//
//
//    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
//        try {
//            url = url.replace("http", "ws");
//            URI socketURI = new URI(url + "/ws");
//            this.notificationHandler = notificationHandler;
//
//            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//            this.session = container.connectToServer(this, socketURI);
//
//            //set message handler
//            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
//                @Override
//                public void onMessage(String message) {
//                    Notification notification = new Gson().fromJson(message, Notification.class);
//                    notificationHandler.notify(notification);
//                }
//            });
//        } catch (DeploymentException | IOException | URISyntaxException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//
//    //Endpoint requires this method, but you don't have to do anything
//    @Override
//    public void onOpen(Session session, EndpointConfig endpointConfig) {
//    }
//
//    public void enterPetShop(String visitorName) throws ResponseException {
//        try {
//            var action = new Action(Action.Type.ENTER, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//
//    public void leavePetShop(String visitorName) throws ResponseException {
//        try {
//            var action = new Action(Action.Type.EXIT, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//            this.session.close();
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//
//}