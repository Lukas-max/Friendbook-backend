package luke.friendbook.connection;

import luke.friendbook.connection.model.PrivateChatMessage;
import luke.friendbook.connection.model.PublicChatMessage;
import luke.friendbook.connection.model.UserData;
import luke.friendbook.connection.services.global.IPublicChatService;
import luke.friendbook.connection.services.p2p.IPrivateChatService;
import luke.friendbook.model.Chunk;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/chat")
public class ChatDataController {

    private final IPrivateChatService privateChatService;
    private final IPublicChatService publicChatService;

    public ChatDataController(IPrivateChatService privateChatService,
                              IPublicChatService publicChatService) {
        this.privateChatService = privateChatService;
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
        Chunk<PrivateChatMessage> chatMessageChunk = privateChatService.findChatMessages(
                senderUUID,
                receiverUUID,
                Integer.parseInt(limit),
                Long.parseLong(offset));

        return ResponseEntity.ok(chatMessageChunk);
    }

    @GetMapping("/pending-messages")
    public ResponseEntity<Set<UserData>> getUserData() {
        Set<UserData> userDataSet = privateChatService.findUserData();
        return ResponseEntity.ok().body(userDataSet);
    }
}
