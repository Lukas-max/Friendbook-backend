package luke.friendbook.connection.services.p2p;

import luke.friendbook.connection.model.ChatRoom;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatRoomService implements IChatRoomService{

    private final IChatRoomRepository chatRoomRepository;

    public ChatRoomService(IChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }


    @Override
    public Optional<String> getChatId(String senderUUID, String receiverUUID, boolean createIfNotExist) {
        return chatRoomRepository.findBySenderUUIDAndReceiverUUID(senderUUID, receiverUUID)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (!createIfNotExist)
                        return Optional.empty();

                    String chatId = String.format("%s_%s", senderUUID, receiverUUID);

                    ChatRoom senderReceiver = ChatRoom.builder()
                            .chatId(chatId)
                            .senderUUID(senderUUID)
                            .receiverUUID(receiverUUID)
                            .build();

                    ChatRoom receiverSender = ChatRoom.builder()
                            .chatId(chatId)
                            .senderUUID(receiverUUID)
                            .receiverUUID(senderUUID)
                            .build();

                    chatRoomRepository.save(senderReceiver);
                    chatRoomRepository.save(receiverSender);

                    return Optional.of(chatId);
                });
    }
}
