package websocket.messages;

public class ErrorMessage extends ServerMessage{
    String errMessage;
    public ErrorMessage(String errorMessage){
        super(ServerMessageType.ERROR);
        errMessage = errorMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }
}
