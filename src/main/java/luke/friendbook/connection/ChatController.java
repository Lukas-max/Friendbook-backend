package luke.friendbook.connection;

import luke.friendbook.connection.model.ConnectedUser;
import luke.friendbook.connection.model.PrivateChatMessage;
import luke.friendbook.connection.model.PublicChatMessage;
import luke.friendbook.connection.services.global.IPublicChatService;
import luke.friendbook.connection.services.p2p.IChatRoomService;
import luke.friendbook.connection.services.p2p.IPrivateChatMessageService;
import luke.friendbook.exception.ChatRoomException;
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
    private final IChatRoomService chatRoomService;
    private final IPrivateChatMessageService privateChatMessageService;
    private final Set<ConnectedUser> users = new HashSet<>();

    public ChatController(SimpMessageSendingOperations messageTemplate,
                          IPublicChatService publicChatService,
                          IChatRoomService chatRoomService,
                          IPrivateChatMessageService privateChatMessageService) {
        this.messageTemplate = messageTemplate;
        this.publicChatService = publicChatService;
        this.chatRoomService = chatRoomService;
        this.privateChatMessageService = privateChatMessageService;
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
        String chatId = chatRoomService.getChatId(
                privateChatMessage.getSenderUUID(),
                privateChatMessage.getReceiverUUID(),
                true)
                .orElseThrow(() -> new ChatRoomException("Nie udało się utworzyć pokoju dla czatu."));

        messageTemplate.convertAndSend("/topic/private." + privateChatMessage.getReceiverUUID(), privateChatMessage);

        String encodedNotification = Base64.getEncoder().encodeToString(privateChatMessage.getContent().getBytes());
        privateChatMessage.setContent(encodedNotification);
        privateChatMessage.setChatId(chatId);
        privateChatMessageService.save(privateChatMessage);
    }
}















