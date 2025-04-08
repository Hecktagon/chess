package websocket.messages;

import javax.management.Notification;

public class NotificationMessage extends ServerMessage{
    String notification;
    public NotificationMessage(String notificationMessage){
        super(ServerMessageType.NOTIFICATION);
        notification = notificationMessage;
    }
}
