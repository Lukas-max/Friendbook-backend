package luke.friendbook.connection.services.p2p;

import luke.friendbook.connection.model.PrivateChatMessage;
import luke.friendbook.model.Chunk;

import java.util.List;

public interface IPrivateChatRepository {

    List<PrivateChatMessage> findByChatId(String chatId);

    Chunk<PrivateChatMessage> findChunk(int limit, long offset, String chatId);

    List<PrivateChatMessage> findPendingMessages();

    PrivateChatMessage save(PrivateChatMessage privateChatMessage);

    void updateMessagesToReceivedStatus(String chatId, String userUUID);
}
