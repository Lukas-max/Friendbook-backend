package luke.friendbook.connection.services.p2p;

import luke.friendbook.connection.model.PrivateChatMessage;
import luke.friendbook.model.Chunk;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class PrivateChatService implements IPrivateChatService {

    private final IPrivateChatRepository privateChatMessageRepository;
    private final IChatRoomService chatRoomService;

    public PrivateChatService(IPrivateChatRepository privateChatMessageRepository,
                              IChatRoomService chatRoomService) {
        this.privateChatMessageRepository = privateChatMessageRepository;
        this.chatRoomService = chatRoomService;
    }


    @Override
    public PrivateChatMessage save(PrivateChatMessage privateChatMessage) {
        return privateChatMessageRepository.save(privateChatMessage);
    }

    @Override
    public Chunk<PrivateChatMessage> findChatMessages(String senderUUID, String receiverUUID, int limit, long offset) {
        Optional<String> chatId = chatRoomService.getChatId(senderUUID, receiverUUID, false);

        if (chatId.isEmpty())
            return new Chunk<>(limit, offset, Collections.emptyList());

        return privateChatMessageRepository.findChunk(limit, offset, chatId.get());
    }
}















