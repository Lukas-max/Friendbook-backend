package luke.friendbook.connection.services.global;

import luke.friendbook.model.Chunk;
import luke.friendbook.model.Repository;
import luke.friendbook.connection.model.PublicChatMessage;

public interface IPublicChatRepository extends Repository<PublicChatMessage> {

    Chunk<PublicChatMessage> findChunk(int limit, long offset);
}
