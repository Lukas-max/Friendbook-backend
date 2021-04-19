package luke.friendbook.connection.services;

import luke.friendbook.connection.model.PublicChatMessage;
import org.springframework.stereotype.Service;

@Service
public class PublicChatService implements IPublicChatService{

    private final IPublicChatRepository chatRepository;

    public PublicChatService(IPublicChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public void saveMessage(PublicChatMessage publicChatMessage) {
        chatRepository.save(publicChatMessage);
    }
}
