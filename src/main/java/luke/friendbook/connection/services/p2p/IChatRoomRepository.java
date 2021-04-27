package luke.friendbook.connection.services.p2p;

import luke.friendbook.model.Repository;
import luke.friendbook.connection.model.ChatRoom;

import java.util.Optional;

public interface IChatRoomRepository extends Repository<ChatRoom> {

    Optional<ChatRoom> findBySenderUUIDAndReceiverUUID(String senderUUID, String receiverUUID);
}
