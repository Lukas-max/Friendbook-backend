package luke.friendbook.connection.services.p2p;

import luke.friendbook.account.model.User;
import luke.friendbook.account.services.IUserService;
import luke.friendbook.connection.model.MessageStatus;
import luke.friendbook.connection.model.PrivateChatMessage;
import luke.friendbook.connection.model.UserData;
import luke.friendbook.model.Chunk;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PrivateChatService implements IPrivateChatService {

    private final IPrivateChatRepository privateChatRepository;
    private final IChatRoomService chatRoomService;
    private final IUserService userService;

    public PrivateChatService(IPrivateChatRepository privateChatRepository,
                              IChatRoomService chatRoomService,
                              IUserService userService) {
        this.privateChatRepository = privateChatRepository;
        this.chatRoomService = chatRoomService;
        this.userService = userService;
    }


    @Override
    public PrivateChatMessage save(PrivateChatMessage privateChatMessage) {
        setMessageStatus(privateChatMessage);
        return privateChatRepository.save(privateChatMessage);
    }

    @Override
    public Chunk<PrivateChatMessage> findChatMessages(String senderUUID, String receiverUUID, int limit, long offset) {
        Optional<String> optionalChatId = chatRoomService.getChatId(senderUUID, receiverUUID, false);

        if (optionalChatId.isEmpty())
            return new Chunk<>(limit, offset, Collections.emptyList());

        String chatId = optionalChatId.get();
        privateChatRepository.updateMessagesToReceivedStatus(chatId, senderUUID);
        return privateChatRepository.findChunk(limit, offset, chatId);
    }

    @Override
    public Set<UserData> findUserData() {
        List<PrivateChatMessage> chatMessageList = privateChatRepository.findPendingMessages();
        Set<UserData> userDataSet = new HashSet<>();

        chatMessageList.forEach(privateChatMessage -> {
            userDataSet.add(new UserData(
                    privateChatMessage.getSenderName(),
                    privateChatMessage.getSenderUUID(),
                    true
            ));
        });
        return userDataSet;
    }

    private void setMessageStatus(PrivateChatMessage privateChatMessage) {
        User user = userService.getUserByUUID(privateChatMessage.getReceiverUUID());

        if (user.isLogged())
            privateChatMessage.setStatus(MessageStatus.RECEIVED);
        else
            privateChatMessage.setStatus(MessageStatus.PENDING);
    }
}















