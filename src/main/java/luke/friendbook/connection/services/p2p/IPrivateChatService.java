package luke.friendbook.connection.services.p2p;

import luke.friendbook.connection.model.PrivateChatMessage;
import luke.friendbook.connection.model.UserData;
import luke.friendbook.model.Chunk;

import java.util.Set;

public interface IPrivateChatService {

    Chunk<PrivateChatMessage> findChatMessages(String senderUUID, String receiverUUID, int limit, long offset);

    Set<UserData> findUserData();

    PrivateChatMessage save(PrivateChatMessage privateChatMessage);

}
