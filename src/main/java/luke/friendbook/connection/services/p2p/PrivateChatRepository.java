package luke.friendbook.connection.services.p2p;

import luke.friendbook.connection.model.MessageStatus;
import luke.friendbook.connection.model.PrivateChatMessage;
import luke.friendbook.model.Chunk;
import luke.friendbook.utilities.Utils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class PrivateChatRepository implements IPrivateChatRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<PrivateChatMessage> findByChatId(String chatId) {
        final String query = "SELECT m FROM PrivateChatMessage m WHERE m.chatId = ?1";
        return entityManager.createQuery(query, PrivateChatMessage.class)
                .setParameter(1, chatId)
                .getResultList();
    }

    @Override
    public Chunk<PrivateChatMessage> findChunk(int limit, long offset, String chatId) {
        final String query = "SELECT * FROM private_chat WHERE chat_id = ?1 ORDER BY id DESC LIMIT ?2 OFFSET ?3";
        Query chatQuery =  entityManager.createNativeQuery(query, PrivateChatMessage.class)
                .setParameter(1, chatId)
                .setParameter(2, limit)
                .setParameter(3, offset);

        @SuppressWarnings("unchecked")
        List<PrivateChatMessage> chatMessageList = (List<PrivateChatMessage>) chatQuery.getResultList();
        return new Chunk<>(limit, offset, chatMessageList);
    }

    @Override
    public List<PrivateChatMessage> findPendingMessages() {
        String userUUID = Utils.getAuthenticatedUser().getUser().getUserUUID();
        final String query = "SELECT m FROM PrivateChatMessage m WHERE m.receiverUUID = ?1 AND m.status = ?2";
        TypedQuery<PrivateChatMessage> typedQuery = entityManager.createQuery(query, PrivateChatMessage.class)
                .setParameter(1, userUUID)
                .setParameter(2, MessageStatus.PENDING);

        return typedQuery.getResultList();
    }

    @Override
    @Transactional
    public PrivateChatMessage save(PrivateChatMessage privateChatMessage) {
        entityManager.persist(privateChatMessage);
        return privateChatMessage;
    }

    @Override
    @Transactional
    public void updateMessagesToReceivedStatus(String chatId, String userUUID) {
        final String stringQuery = "UPDATE PrivateChatMessage m SET m.status = ?1 " +
                "WHERE m.chatId = ?2 AND m.receiverUUID = ?3 AND m.status = ?4";
        Query query = entityManager.createQuery(stringQuery)
                .setParameter(1, MessageStatus.RECEIVED)
                .setParameter(2, chatId)
                .setParameter(3, userUUID)
                .setParameter(4, MessageStatus.PENDING);

        query.executeUpdate();
    }

    @Override
    @Transactional
    public void deleteMessagesBySenderUUID(String userUUID) {
        final String jpql = "DELETE FROM PrivateChatMessage m WHERE m.senderUUID = ?1";
        Query query = entityManager.createQuery(jpql)
                .setParameter(1, userUUID);
        query.executeUpdate();
    }
}
