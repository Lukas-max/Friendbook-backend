package luke.friendbook.connection;

import luke.friendbook.connection.model.PrivateChatMessage;
import luke.friendbook.connection.model.PublicChatMessage;
import luke.friendbook.connection.services.global.IPublicChatService;
import luke.friendbook.connection.services.p2p.IPrivateChatService;
import luke.friendbook.model.Chunk;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatDataController {

    private final IPrivateChatService privateChatMessageService;
    private final IPublicChatService publicChatService;

    public ChatDataController(IPrivateChatService privateChatMessageService,
                              IPublicChatService publicChatService) {
        this.privateChatMessageService = privateChatMessageService;
        this.publicChatService = publicChatService;
    }

    @GetMapping
    public ResponseEntity<Chunk<PublicChatMessage>> findPublicChatMessages(@RequestParam String limit,
                                                                           @RequestParam String offset) {
        Chunk<PublicChatMessage> chatMessageChunk = publicChatService.findChatChunk(
                Integer.parseInt(limit),
                Long.parseLong(offset)
        );
        return ResponseEntity.ok().body(chatMessageChunk);
    }

    @GetMapping("/{senderUUID}/{receiverUUID}")
    public ResponseEntity<Chunk<PrivateChatMessage>> findPrivateChatMessages(@PathVariable String senderUUID,
                                                                             @PathVariable String receiverUUID,
                                                                             @RequestParam String limit,
                                                                             @RequestParam String offset) {
        Chunk<PrivateChatMessage> chatMessageChunk = privateChatMessageService.findChatMessages(
                senderUUID,
                receiverUUID,
                Integer.parseInt(limit),
                Long.parseLong(offset));

        return ResponseEntity.ok(chatMessageChunk);
    }
}
