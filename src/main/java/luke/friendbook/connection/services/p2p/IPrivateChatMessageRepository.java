package luke.friendbook.connection.services.p2p;

import luke.friendbook.connection.model.PrivateChatMessage;

import java.util.List;

public interface IPrivateChatMessageRepository {

    List<PrivateChatMessage> findByChatId(String chatId);

    PrivateChatMessage save(PrivateChatMessage privateChatMessage);
}
