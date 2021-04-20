package luke.friendbook.connection.services.p2p;

import luke.friendbook.connection.model.PrivateChatMessage;

import java.util.List;

public interface IPrivateChatMessageService {

    PrivateChatMessage save(PrivateChatMessage privateChatMessage);


    List<PrivateChatMessage> findChatMessages(String senderUUID, String receiverUUID);
}
