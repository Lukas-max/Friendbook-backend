package luke.friendbook.connection.services.p2p;

import luke.friendbook.connection.model.ChatRoom;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class ChatRoomRepository implements IChatRoomRepository {

    @PersistenceContext
    private EntityManager entityManager;



    @Override
    public List<ChatRoom> findAll() {
        final String query = "SELECT r FROM ChatRoom r";
        TypedQuery<ChatRoom> userTypedQuery = entityManager.createQuery(query, ChatRoom.class);
        return userTypedQuery.getResultList();
    }

    @Override
    public Optional<ChatRoom> findById(Long id) {
        ChatRoom chatRoom = entityManager.find(ChatRoom.class, id);
        if (chatRoom == null)
            return Optional.empty();

        return Optional.of(chatRoom);
    }

    @Override
    public Optional<ChatRoom> findBySenderUUIDAndReceiverUUID(String senderUUID, String receiverUUID) {
        final String query = "SELECT r FROM ChatRoom r WHERE r.senderUUID = ?1 AND r.receiverUUID = ?2";
        TypedQuery<ChatRoom> roomTypedQuery = entityManager.createQuery(query, ChatRoom.class)
                .setParameter(1, senderUUID)
                .setParameter(2, receiverUUID);

        ChatRoom chatRoom;

        try {
            chatRoom = roomTypedQuery.getSingleResult();
        } catch (NoResultException e) {
            return Optional.empty();
        }
        return Optional.of(chatRoom);
    }

    @Override
    @Transactional
    public ChatRoom save(ChatRoom chatRoom) {
        entityManager.persist(chatRoom);
        return chatRoom;
    }

    @Override
    @Transactional
    public Iterable<ChatRoom> saveAll(Iterable<ChatRoom> rooms) {
        rooms.forEach(this::save);
        return rooms;
    }

    @Override
    @Transactional
    public ChatRoom update(ChatRoom chatRoom) {
        ChatRoom fetchedRoom = entityManager.find(ChatRoom.class, chatRoom.getId());
        if (fetchedRoom != null) {
            fetchedRoom.setSenderUUID(chatRoom.getSenderUUID());
            fetchedRoom.setReceiverUUID(chatRoom.getSenderUUID());
        }
        return fetchedRoom;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        ChatRoom room = entityManager.find(ChatRoom.class, id);
        if (room != null) {
            entityManager.remove(room);
            return true;
        }

        return false;
    }
}
