package luke.friendbook.connection;

import luke.friendbook.connection.model.ConnectedUser;
import luke.friendbook.connection.model.PrivateChatMessage;
import luke.friendbook.connection.model.PublicChatMessage;
import luke.friendbook.connection.services.IPublicChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Controller
public class ChatController {

    private final SimpMessageSendingOperations messageTemplate;
    private final IPublicChatService publicChatService;
    private final Set<ConnectedUser> users = new HashSet<>();

    public ChatController(SimpMessageSendingOperations messageTemplate, IPublicChatService publicChatService) {
        this.messageTemplate = messageTemplate;
        this.publicChatService = publicChatService;
    }

    @MessageMapping("/logged")
    public void connectMessage(@Payload ConnectedUser connectedUser) {
        users.add(connectedUser);
        messageTemplate.convertAndSend("/topic/connection", users);
    }

    @MessageMapping("/exit")
    public void exitConnectionMessage(@Payload ConnectedUser connectedUser) {
        users.remove(connectedUser);
        messageTemplate.convertAndSend("/topic/connection", users);
    }

    @MessageMapping("/public")
    public void send(@Payload PublicChatMessage publicChatMessage) {
        publicChatService.saveMessage(publicChatMessage);
        messageTemplate.convertAndSend("/topic/public", publicChatMessage);
    }

    @MessageMapping("/private")
    public void sendPrivate(@Payload PrivateChatMessage privateChatMessage) {
        String encodedNotification = Base64.getEncoder().encodeToString(privateChatMessage.getContent().getBytes());
        System.out.println(encodedNotification);
        messageTemplate.convertAndSend("/topic/private." + privateChatMessage.getReceiverUUID(), privateChatMessage);
    }
}















