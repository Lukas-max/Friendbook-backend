package luke.friendbook.mainFeed.services;

import luke.friendbook.mainFeed.model.FeedModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class FeedRepository implements IFeedRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @Transactional
    public List<FeedModel> findAll() {
        final String query = "SELECT f FROM FeedModel f";
        TypedQuery<FeedModel> userTypedQuery = entityManager.createQuery(query, FeedModel.class);
        return userTypedQuery.getResultList();
    }

    @Override
    @Transactional
    public Optional<FeedModel> findById(Long id) {
        final String query = "SELECT f FROM FeedModel f WHERE f.id = ?1";
        TypedQuery<FeedModel> userTypedQuery = entityManager.createQuery(query, FeedModel.class);
        userTypedQuery.setParameter(1, id);
        FeedModel feed;

        try {
            feed = userTypedQuery.getSingleResult();
        } catch (NoResultException e) {
            return Optional.empty();
        }
        return Optional.of(feed);
    }

    @Override
    @Transactional
    public FeedModel save(FeedModel feedModel) {
        return entityManager.merge(feedModel);
    }

    @Override
    @Transactional
    public Iterable<FeedModel> saveAll(Iterable<FeedModel> feeds) {
        feeds.forEach(this::save);
        return feeds;
    }

    @Override
    @Transactional
    public FeedModel update(FeedModel feedModel) {
        return null;
    }

    @Override
    @Transactional
    public boolean deleteById(Long feedId) {
        FeedModel feed = entityManager.find(FeedModel.class, feedId);
        if (feed != null) {
            entityManager.remove(feed);
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public void deleteFeed(FeedModel feedModel) {
        entityManager.remove(feedModel);
    }
}
