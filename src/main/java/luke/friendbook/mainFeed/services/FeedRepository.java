package luke.friendbook.mainFeed.services;

import luke.friendbook.account.model.User;
import luke.friendbook.mainFeed.model.FeedModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class FeedRepository implements IFeedRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<FeedModel> findAll() {
        final String query = "SELECT f FROM FeedModel f";
        TypedQuery<FeedModel> userTypedQuery = entityManager.createQuery(query, FeedModel.class);
        return userTypedQuery.getResultList();
    }

    @Override
    public Optional<FeedModel> findById(Long id) {
        return Optional.empty();
    }

    @Override
    @Transactional
    public FeedModel save(FeedModel feedModel) {
        return entityManager.merge(feedModel);
    }

    @Override
    @Transactional
    public Iterable<FeedModel> saveAll(Iterable<FeedModel> t) {
        return null;
    }

    @Override
    @Transactional
    public FeedModel update(FeedModel feedModel) {
        return null;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return false;
    }
}
