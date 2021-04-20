package luke.friendbook.connection.services.p2p;

import luke.friendbook.connection.model.PrivateChatMessage;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PrivateChatMessageService implements IPrivateChatMessageService{

    private final IPrivateChatMessageRepository privateChatMessageRepository;
    private final IChatRoomService chatRoomService;

    public PrivateChatMessageService(IPrivateChatMessageRepository privateChatMessageRepository,
                                     IChatRoomService chatRoomService) {
        this.privateChatMessageRepository = privateChatMessageRepository;
        this.chatRoomService = chatRoomService;
    }


    @Override
    public PrivateChatMessage save(PrivateChatMessage privateChatMessage) {
        return privateChatMessageRepository.save(privateChatMessage);
    }

    @Override
    public List<PrivateChatMessage> findChatMessages(String senderUUID, String receiverUUID) {
        Optional<String> chatId = chatRoomService.getChatId(senderUUID, receiverUUID, false);

        if (chatId.isEmpty())
            return Collections.emptyList();

        return privateChatMessageRepository.findByChatId(chatId.get());
    }
}















