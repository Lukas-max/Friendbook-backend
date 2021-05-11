package luke.friendbook.connection.services.global;

import luke.friendbook.connection.model.PublicChatMessage;
import luke.friendbook.model.Chunk;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class PublicChatRepository implements IPublicChatRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PublicChatMessage> findAll() {
        final String query = "SELECT m FROM PublicChatMessage m";
        TypedQuery<PublicChatMessage> userTypedQuery = entityManager.createQuery(query, PublicChatMessage.class);
        return userTypedQuery.getResultList();
    }

    @Override
    public Chunk<PublicChatMessage> findChunk(int limit, long offset) {
        final String query = "SELECT * FROM public_chat ORDER BY id DESC LIMIT ?1 OFFSET ?2";
        Query chatQuery =  entityManager.createNativeQuery(query, PublicChatMessage.class)
                .setParameter(1, limit)
                .setParameter(2, offset);

        @SuppressWarnings("unchecked")
        List<PublicChatMessage> chatMessageList = (List<PublicChatMessage>) chatQuery.getResultList();
        return new Chunk<>(limit, offset, chatMessageList);
    }

    @Override
    public Optional<PublicChatMessage> findById(Long id) {
        PublicChatMessage chatMessage = entityManager.find(PublicChatMessage.class, id);
        if (chatMessage == null)
            return Optional.empty();

        return Optional.of(chatMessage);
    }

    @Override
    @Transactional
    public PublicChatMessage save(PublicChatMessage publicChatMessage) {
        entityManager.persist(publicChatMessage);
        return publicChatMessage;
    }

    @Override
    @Transactional
    public Iterable<PublicChatMessage> saveAll(Iterable<PublicChatMessage> chatMessages) {
        chatMessages.forEach(this::save);
        return chatMessages;
    }

    @Override
    @Transactional
    public PublicChatMessage update(PublicChatMessage publicChatMessage) {
        PublicChatMessage chatMessage = entityManager.find(PublicChatMessage.class, publicChatMessage.getId());
        if (chatMessage != null){
            chatMessage.setContent(publicChatMessage.getContent());
            chatMessage.setTimestamp(publicChatMessage.getTimestamp());
        }
        return chatMessage;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        PublicChatMessage chatMessage = entityManager.find(PublicChatMessage.class, id);
        if (chatMessage != null){
            entityManager.remove(chatMessage);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void deleteAllByUser(String userUUID) {
        final String jpql = "DELETE FROM PublicChatMessage m WHERE m.userUUID = ?1";
        Query query = entityManager.createQuery(jpql)
                .setParameter(1, userUUID);
        query.executeUpdate();
    }
}
