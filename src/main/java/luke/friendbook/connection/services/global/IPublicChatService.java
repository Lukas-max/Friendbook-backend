package luke.friendbook.connection.services.global;

import luke.friendbook.connection.model.PublicChatMessage;
import luke.friendbook.model.Chunk;

public interface IPublicChatService {

    Chunk<PublicChatMessage> findChatChunk(int limit, long offset);

    void saveMessage(PublicChatMessage publicChatMessage);
}
