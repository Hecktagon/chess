package websocket.messages;

import javax.management.Notification;

public class NotificationMessage extends ServerMessage{
    String message;
    public NotificationMessage(String notificationMessage){
        super(ServerMessageType.NOTIFICATION);
        message = notificationMessage;
    }
}
