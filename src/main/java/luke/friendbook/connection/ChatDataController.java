package luke.friendbook.connection;

import luke.friendbook.connection.model.PrivateChatMessage;
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

    public ChatDataController(IPrivateChatMessageService privateChatMessageService) {
        this.privateChatMessageService = privateChatMessageService;
    }

    @GetMapping("/{senderUUID}/{receiverUUID}")
    public ResponseEntity<List<PrivateChatMessage>> findChatMessages(@PathVariable String senderUUID,
                                                                     @PathVariable String receiverUUID) {

        List<PrivateChatMessage> messages = privateChatMessageService.findChatMessages(senderUUID, receiverUUID);
        return ResponseEntity.ok(messages);
    }
}
