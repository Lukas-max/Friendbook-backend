package luke.friendbook.connection.services.p2p;

import luke.friendbook.connection.model.PrivateChatMessage;
import luke.friendbook.connection.model.PublicChatMessage;
import luke.friendbook.model.Chunk;

import java.util.List;

public interface IPrivateChatService {

    Chunk<PrivateChatMessage> findChatMessages(String senderUUID, String receiverUUID, int limit, long offset);

    PrivateChatMessage save(PrivateChatMessage privateChatMessage);

}
