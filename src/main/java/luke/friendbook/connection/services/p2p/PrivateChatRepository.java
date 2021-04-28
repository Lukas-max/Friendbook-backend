package luke.friendbook.connection.services.p2p;

import luke.friendbook.connection.model.PrivateChatMessage;
import luke.friendbook.connection.model.PublicChatMessage;
import luke.friendbook.model.Chunk;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
    @Transactional
    public PrivateChatMessage save(PrivateChatMessage privateChatMessage) {
        entityManager.persist(privateChatMessage);
        return privateChatMessage;
    }
}
