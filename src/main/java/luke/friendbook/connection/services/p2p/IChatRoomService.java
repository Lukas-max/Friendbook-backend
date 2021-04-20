package luke.friendbook.connection.services.p2p;

import java.util.Optional;

public interface IChatRoomService {

    Optional<String> getChatId(String senderUUID, String receiverUUID, boolean createIfNotExist);
}
