package luke.friendbook.connection;

import luke.friendbook.connection.model.ChatModel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessageSendingOperations messageTemplate;

    public ChatController(SimpMessageSendingOperations messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @MessageMapping("/send")
    public void send(@Payload ChatModel chatModel) {
        System.out.println(chatModel.toString());
        messageTemplate.convertAndSend("/topic/public", chatModel);
    }
}
