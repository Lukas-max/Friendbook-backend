package luke.friendbook.mainFeed.services;

import luke.friendbook.mainFeed.model.FeedComment;
import luke.friendbook.model.Chunk;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


@Repository
public class FeedCommentRepository implements IFeedCommentRepository {

    @PersistenceContext
    private EntityManager em;


    @Override
    public List<FeedComment> findAll() {
        final String query = "SELECT c FROM FeedComment c";
        TypedQuery<FeedComment> feedCommentTypedQuery = em.createQuery(query, FeedComment.class);
        return feedCommentTypedQuery.getResultList();
    }

    @Override
    public Optional<FeedComment> findById(Long id) {
        FeedComment comment = em.find(FeedComment.class, id);
        if (comment == null)
            return Optional.empty();

        return Optional.of(comment);
    }

    @Override
    public Chunk<FeedComment> findChunk(long feedId, int limit, long offset) {
        final String query = "SELECT * FROM feed_comment WHERE feed_id =?1 ORDER BY id DESC LIMIT ?2 OFFSET ?3";
        Query commentQuery = em.createNativeQuery(query, FeedComment.class)
                .setParameter(1, feedId)
                .setParameter(2, limit)
                .setParameter(3, offset);

        @SuppressWarnings("unchecked")
        List<FeedComment> comments = (List<FeedComment>) commentQuery.getResultList();
        return new Chunk<>(limit, offset, comments);
    }

    @Override
    @Transactional
    public FeedComment save(FeedComment feedComment) {
        return em.merge(feedComment);
    }

    @Override
    @Transactional
    public Iterable<FeedComment> saveAll(Iterable<FeedComment> comments) {
        comments.forEach(this::save);
        return comments;
    }

    @Override
    @Transactional
    public FeedComment update(FeedComment feedComment) {
        FeedComment fetchedComment = em.find(FeedComment.class, feedComment.getId());
        if (fetchedComment != null){
            fetchedComment.setContent(feedComment.getContent());
            fetchedComment.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        }
        return fetchedComment;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        FeedComment comment = em.find(FeedComment.class, id);
        if (comment != null) {
            em.remove(comment);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void deleteCommentsFromFeed(Long feedId) {
        final String query = "DELETE FROM FeedComment c WHERE c.feedId = ?1";
        Query deleteQuery = em.createQuery(query)
                .setParameter(1, feedId);

        deleteQuery.executeUpdate();
    }

    @Override
    @Transactional
    public void deleteCommentsByUser(String userUUID) {
        final String sql = "DELETE FROM FeedComment c WHERE c.userUUID = ?1";
        Query deleteQuery = em.createQuery(sql)
                .setParameter(1, userUUID);
        deleteQuery.executeUpdate();
    }
}
