package luke.friendbook.connection.services.global;

import luke.friendbook.connection.model.PublicChatMessage;
import luke.friendbook.model.Chunk;
import org.springframework.stereotype.Service;

@Service
public class PublicChatService implements IPublicChatService{

    private final IPublicChatRepository chatRepository;

    public PublicChatService(IPublicChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public Chunk<PublicChatMessage> findChatChunk(int limit, long offset) {
        return chatRepository.findChunk(limit, offset);
    }

    @Override
    public void saveMessage(PublicChatMessage publicChatMessage) {
        chatRepository.save(publicChatMessage);
    }
}
