package websocket.messages;

public class ErrorMessage extends ServerMessage{
    String errorMessage;
    public ErrorMessage(String errMessage){
        super(ServerMessageType.ERROR);
        errorMessage = errMessage;
    }

    public String getErrMessage() {
        return errorMessage;
    }
}
