package luke.friendbook.connection;

import luke.friendbook.account.services.IUserService;
import luke.friendbook.connection.model.ConnectedUser;
import luke.friendbook.connection.model.PrivateChatMessage;
import luke.friendbook.connection.model.PublicChatMessage;
import luke.friendbook.connection.services.global.IPublicChatService;
import luke.friendbook.connection.services.p2p.IChatRoomService;
import luke.friendbook.connection.services.p2p.IPrivateChatService;
import luke.friendbook.exception.model.ChatRoomException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Controller
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;
    private final IPublicChatService publicChatService;
    private final IChatRoomService chatRoomService;
    private final IPrivateChatService privateChatService;
    private final IUserService userService;
    private final Set<ConnectedUser> users = new HashSet<>();

    public StompController(SimpMessageSendingOperations messageTemplate,
                           IPublicChatService publicChatService,
                           IChatRoomService chatRoomService,
                           IPrivateChatService privateChatService,
                           IUserService userService) {
        this.messageTemplate = messageTemplate;
        this.publicChatService = publicChatService;
        this.chatRoomService = chatRoomService;
        this.privateChatService = privateChatService;
        this.userService = userService;
    }

    @MessageMapping("/logged")
    public void connectMessage(@Payload ConnectedUser connectedUser) {
        users.add(connectedUser);
        userService.updateUserStatus(connectedUser.getUserUUID(), true);
        messageTemplate.convertAndSend("/topic/connection", users);
    }

    @MessageMapping("/exit")
    public void exitConnectionMessage(@Payload ConnectedUser connectedUser) {
        users.remove(connectedUser);
        userService.updateUserStatus(connectedUser.getUserUUID(), false);
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

        String encodedNotification = Base64.getEncoder()
                .encodeToString(privateChatMessage.getContent().getBytes(StandardCharsets.UTF_8));
        privateChatMessage.setContent(encodedNotification);
        privateChatMessage.setChatId(chatId);
        privateChatService.save(privateChatMessage);
    }
}















