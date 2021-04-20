package luke.friendbook.connection.services.p2p;

import luke.friendbook.connection.model.PrivateChatMessage;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class PrivateChatMessageRepository implements IPrivateChatMessageRepository {

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
    @Transactional
    public PrivateChatMessage save(PrivateChatMessage privateChatMessage) {
        entityManager.persist(privateChatMessage);
        return privateChatMessage;
    }
}
