package luke.friendbook.connection;

import luke.friendbook.connection.model.PrivateChatMessage;
import luke.friendbook.connection.services.global.IPublicChatService;
import luke.friendbook.connection.services.p2p.IPrivateChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatDataController {

    private final IPrivateChatMessageService privateChatMessageService;
    private final IPublicChatService publicChatService;

    public ChatDataController(IPrivateChatMessageService privateChatMessageService,
                              IPublicChatService publicChatService) {
        this.privateChatMessageService = privateChatMessageService;
        this.publicChatService = publicChatService;
    }

    @GetMapping("/{senderUUID}/{receiverUUID}")
    public ResponseEntity<List<PrivateChatMessage>> findPrivateChatMessages(@PathVariable String senderUUID,
                                                                            @PathVariable String receiverUUID) {

        List<PrivateChatMessage> messages = privateChatMessageService.findChatMessages(senderUUID, receiverUUID);
        return ResponseEntity.ok(messages);
    }
}
