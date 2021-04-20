package luke.friendbook.connection.services.global;

import luke.friendbook.connection.model.PublicChatMessage;

public interface IPublicChatService {

    void saveMessage(PublicChatMessage publicChatMessage);
}
